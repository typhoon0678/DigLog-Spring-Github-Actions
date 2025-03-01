package api.store.diglog.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import api.store.diglog.model.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

}
