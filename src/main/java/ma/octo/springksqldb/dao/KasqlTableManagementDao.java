package ma.octo.springksqldb.dao;

import ma.octo.springksqldb.bo.KasqlTableManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KasqlTableManagementDao extends JpaRepository<KasqlTableManagement, Long> {
    Optional<KasqlTableManagement> findByFileName(String tableName);
}
