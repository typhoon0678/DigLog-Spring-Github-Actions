package api.store.diglog.service;

import static api.store.diglog.common.exception.ErrorCode.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.store.diglog.common.exception.CustomException;
import api.store.diglog.model.dto.folder.FolderCreateRequest;
import api.store.diglog.model.dto.folder.FolderResponse;
import api.store.diglog.model.entity.Folder;
import api.store.diglog.model.entity.Member;
import api.store.diglog.repository.FolderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FolderService {

    private static final int MAX_FOLDER_SIZE = 100;
    private static final String UNDER_BAR_SIGN = "_";

    private final FolderRepository folderRepository;
    private final MemberService memberService;

    @Transactional
    public List<FolderResponse> createAndUpdateFolders(List<FolderCreateRequest> folderCreateRequests) {

        validateDuplicationParentFolderAndTitle(folderCreateRequests);
        validateFolderSize(folderCreateRequests);
        validateDuplicationOrderIndex(folderCreateRequests);

        Member member = memberService.getCurrentMember();
        Map<Integer, Folder> allFolders = new TreeMap<>();

        int maxDepth = calculateMaxDepth(folderCreateRequests);
        IntStream.range(0, maxDepth + 1)
                .forEach(depth -> {
                    Map<Integer, Folder> foldersAtCurrentDepth = createAndUpdateFoldersByDepth(
                            folderCreateRequests,
                            member,
                            depth,
                            allFolders
                    );

                    allFolders.putAll(foldersAtCurrentDepth);
                });

        List<Folder> savedFolders = folderRepository.saveAll(allFolders.values());
        return savedFolders.stream()
                .map(folder -> FolderResponse.builder()
                        .folder(folder)
                        .build())
                .toList();
    }

    private void validateDuplicationParentFolderAndTitle(List<FolderCreateRequest> folderCreateRequests) {

        Set<String> uniqueFolders = new HashSet<>();

        folderCreateRequests.forEach(request -> {
            if (!uniqueFolders.add(request.getParentOrderIndex() + UNDER_BAR_SIGN + request.getTitle())) {
                throw new CustomException(
                        FOLDER_DUPLICATION_TITLE,
                        FOLDER_DUPLICATION_TITLE.getMessage()
                );
            }
        });
    }

    private void validateFolderSize(List<FolderCreateRequest> folderCreateRequests) {
        if (folderCreateRequests.size() > MAX_FOLDER_SIZE) {
            throw new CustomException(
                    FOLDER_OVER_FLOW_SIZE,
                    String.format(FOLDER_OVER_FLOW_SIZE.getMessage(), MAX_FOLDER_SIZE)
            );
        }
    }

    private void validateDuplicationOrderIndex(List<FolderCreateRequest> folderCreateRequests) {

        Set<Integer> uniqueOrderIndexes = folderCreateRequests.stream()
                .map(FolderCreateRequest::getOrderIndex)
                .collect(Collectors.toSet());

        if (folderCreateRequests.size() != uniqueOrderIndexes.size()) {
            throw new CustomException(
                    FOLDER_DUPLICATION_ORDER_INDEX,
                    FOLDER_DUPLICATION_ORDER_INDEX.getMessage()
            );
        }
    }

    private int calculateMaxDepth(List<FolderCreateRequest> folderCreateRequests) {
        return folderCreateRequests.stream()
                .map(FolderCreateRequest::getDepth)
                .mapToInt(depth -> depth)
                .max()
                .orElse(0);
    }

    private Map<Integer, Folder> createAndUpdateFoldersByDepth(
            List<FolderCreateRequest> requests,
            Member member,
            int depth,
            Map<Integer, Folder> existingFolders
    ) {
        return requests.stream()
                .filter(request -> request.getDepth() == depth)
                .collect(Collectors.toMap(
                        FolderCreateRequest::getOrderIndex,
                        request -> createAndUpdateFolder(request, member, existingFolders)
                ));
    }

    private Folder createAndUpdateFolder(
            FolderCreateRequest folderCreateRequest,
            Member member,
            Map<Integer, Folder> existingFolders
    ) {
        Folder parentFolder = null;
        if (folderCreateRequest.getDepth() > 0) {
            parentFolder = existingFolders.get(folderCreateRequest.getParentOrderIndex());
        }

        UUID folderId = folderCreateRequest.getId();
        if (folderId == null) {
            folderId = UUID.randomUUID();
        }

        return Folder.builder()
                .id(folderId)
                .member(member)
                .title(folderCreateRequest.getTitle())
                .depth(folderCreateRequest.getDepth())
                .orderIndex(folderCreateRequest.getOrderIndex())
                .parentFolder(parentFolder)
                .build();
    }

    public Folder getFolderByIdAndMemberId(UUID folderId, UUID memberId) {
        if (folderId == null) {
            return null;
        }

        return folderRepository.findByIdAndMemberId(folderId, memberId)
                .orElseThrow(() -> new CustomException(FOLDER_OWNER_MISMATCH));
    }
}
