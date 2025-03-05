package api.store.diglog.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import api.store.diglog.model.entity.Folder;
import api.store.diglog.model.entity.Member;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

	@Query("SELECT f FROM Folder f LEFT JOIN FETCH f.parentFolder WHERE f.member = :member")
	List<Folder> findAllByMemberWithParent(@Param("member") Member member);
}
