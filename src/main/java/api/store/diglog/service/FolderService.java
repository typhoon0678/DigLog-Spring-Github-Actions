package api.store.diglog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	private static final int MAX_FOLDER_DEPTH = 3;

	private final FolderRepository folderRepository;
	private final MemberService memberService;

	@Transactional
	public List<FolderResponse> createFolders(List<FolderCreateRequest> folderCreateRequests) {
		Member member = memberService.getCurrentMember();
		Map<Integer, Folder> allFolders = new HashMap<>();

		IntStream.range(0, MAX_FOLDER_DEPTH)
			.forEach(depth -> {
				Map<Integer, Folder> foldersAtCurrentDepth = createFoldersByDepth(
					folderCreateRequests,
					member,
					depth,
					allFolders
				);

				allFolders.putAll(foldersAtCurrentDepth);
			});

		List<Folder> savedFolders = folderRepository.saveAll(allFolders.values());
		return savedFolders.stream()
			.map(FolderResponse::from)
			.toList();
	}

	private Map<Integer, Folder> createFoldersByDepth(
		List<FolderCreateRequest> requests,
		Member member,
		int depth,
		Map<Integer, Folder> existingFolders
	) {
		return requests.stream()
			.filter(request -> request.getDepth() == depth)
			.collect(Collectors.toMap(
				FolderCreateRequest::getTmpId,
				request -> createFolder(request, member, existingFolders)
			));
	}

	private Folder createFolder(
		FolderCreateRequest folderCreateRequest,
		Member member,
		Map<Integer, Folder> existingFolders
	) {
		Folder parentFolder = null;
		if (folderCreateRequest.getDepth() > 0) {
			parentFolder = existingFolders.get(folderCreateRequest.getTmpParentId());
		}

		return Folder.builder()
			.id(UUID.randomUUID())
			.member(member)
			.title(folderCreateRequest.getTitle())
			.depth(folderCreateRequest.getDepth())
			.orderIndex(folderCreateRequest.getOrderIndex())
			.parentFolder(parentFolder)
			.build();
	}
}
