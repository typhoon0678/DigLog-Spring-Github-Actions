package api.store.diglog.model.dto.folder;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FolderCreateRequest {

	private UUID id;
	private String title;
	private int depth;
	private int orderIndex;
	private int parentOrderIndex;
}
