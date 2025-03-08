package api.store.diglog.model.dto.folder;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FolderDeleteRequest {

	private UUID folderId;

}
