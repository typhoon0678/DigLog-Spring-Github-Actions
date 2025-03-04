package api.store.diglog.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import api.store.diglog.model.entity.Folder;
import api.store.diglog.model.entity.Member;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

	List<Folder> findAllByMember(Member member);
}
