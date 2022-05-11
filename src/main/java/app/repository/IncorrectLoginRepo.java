package app.repository;

import app.model.IncorrectLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface IncorrectLoginRepo extends JpaRepository<IncorrectLogin, Long> {

    @Query("SELECT i FROM IncorrectLogin i WHERE i.ipAddress = ?1")
    IncorrectLogin findByip(String ipAddress);

    @Transactional
    @Query("UPDATE IncorrectLoggin i SET i.numAttempts = ?1 WHERE i.ipAddress = ?2")
    @Modifying
    public void updateFailedAttempts(int numAttempts, String ipAddress);
}

