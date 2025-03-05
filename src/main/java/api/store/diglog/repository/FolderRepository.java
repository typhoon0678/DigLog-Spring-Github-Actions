package api.store.diglog.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import api.store.diglog.model.dto.folder.FolderPostCountResponse;
import api.store.diglog.model.entity.Folder;
import api.store.diglog.model.entity.Member;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

	@Query("""
		    SELECT new api.store.diglog.model.dto.folder.FolderPostCountResponse(
		        f.id,
		        f.title,
		        f.depth,
		        f.orderIndex,
		        parent.id,
		        COUNT(p.id)
		    )
		    FROM Folder f
		    LEFT JOIN f.parentFolder parent
		    LEFT JOIN Post p ON f.id = p.folder.id
		    WHERE f.member = :member
		    GROUP BY f.id
		    ORDER BY f.orderIndex
		""")
	List<FolderPostCountResponse> findAllWithPostCountByMember(@Param("member") Member member);
}
