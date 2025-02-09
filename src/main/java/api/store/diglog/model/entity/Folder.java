package api.store.diglog.model.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Folder {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TINYINT(3)")
	private int depth;

	@Column(nullable = false)
	private int orderIndex;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Folder parentFolder;
}
