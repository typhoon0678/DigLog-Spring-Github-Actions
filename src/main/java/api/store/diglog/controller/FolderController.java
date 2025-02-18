package api.store.diglog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.store.diglog.model.dto.folder.FolderCreateRequest;
import api.store.diglog.model.dto.folder.FolderResponse;
import api.store.diglog.service.FolderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

	private final FolderService folderService;

	@PostMapping
	public ResponseEntity<List<FolderResponse>> create(@RequestBody List<FolderCreateRequest> folderCreateRequests) {

		List<FolderResponse> folderResponses = folderService.createAndUpdateFolders(folderCreateRequests);
		return ResponseEntity.ok().body(folderResponses);
	}
}
