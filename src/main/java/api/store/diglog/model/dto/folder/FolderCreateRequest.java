package api.store.diglog.model.dto.folder;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FolderCreateRequest {

	private int tmpId;
	private String title;
	private int depth;
	private int orderIndex;
	private int tmpParentId;

}
