package api.store.diglog.model.dto.folder;

import java.util.UUID;

import api.store.diglog.model.entity.Folder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FolderResponse {

	private UUID folderId;
	private String title;
	private int depth;
	private int orderIndex;
	private String parentFolderId;

	public static FolderResponse from(Folder folder) {

		Folder parentFolder = folder.getParentFolder();
		String parentFolderId = "none";
		if (parentFolder != null) {
			parentFolderId = parentFolder.getId().toString();
		}

		return new FolderResponse(
			folder.getId(),
			folder.getTitle(),
			folder.getDepth(),
			folder.getOrderIndex(),
			parentFolderId
		);
	}

}
