package api.store.diglog.model.entity;

import static api.store.diglog.common.exception.ErrorCode.*;

import java.util.UUID;

import api.store.diglog.common.exception.folder.FolderException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "folder",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"member_id", "parent_id", "title"})
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Folder {

	private static final int MAX_DEPTH = 3;
	private static final int MAX_ORDER_INDEX = 100;
	private static final int MAX_TITLE_LENGTH = 25;

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false, length = 25)
	private String title;

	@Column(nullable = false, columnDefinition = "TINYINT(3)")
	private int depth;

	@Column(nullable = false)
	private int orderIndex;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Folder parentFolder;

	@Builder
	private Folder(UUID id, Member member, String title, int depth, int orderIndex, Folder parentFolder) {

		validateDepth(depth);
		validateOrderIndex(orderIndex);
		validateTitleLength(title);

		this.id = id;
		this.member = member;
		this.title = title;
		this.depth = depth;
		this.orderIndex = orderIndex;
		this.parentFolder = parentFolder;
	}

	private void validateDepth(int depth) {

		if (depth >= MAX_DEPTH || depth < 0) {
			throw new FolderException(
				FOLDER_OVER_FLOW_DEPTH.getStatus(),
				String.format(FOLDER_OVER_FLOW_DEPTH.getMessage(), MAX_DEPTH)
			);
		}

	}

	private void validateOrderIndex(int orderIndex) {
		if (orderIndex >= MAX_ORDER_INDEX || orderIndex < 0) {
			throw new FolderException(
				FOLDER_OVER_FLOW_ORDER_INDEX.getStatus(),
				String.format(FOLDER_OVER_FLOW_ORDER_INDEX.getMessage(), MAX_ORDER_INDEX)
			);
		}
	}

	private void validateTitleLength(String title) {
		if (title.length() > MAX_TITLE_LENGTH) {
			throw new FolderException(
				FOLDER_OVER_FLOW_TITLE_LENGTH.getStatus(),
				String.format(FOLDER_OVER_FLOW_TITLE_LENGTH.getMessage(), MAX_TITLE_LENGTH)
			);
		}
	}
}
