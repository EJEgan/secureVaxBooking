package app.repository;

import app.model.IncorrectLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IncorrectLoginRepo extends JpaRepository<IncorrectLogin, Long> {

    @Query("SELECT i FROM IncorrectLogin i WHERE i.ipAddress = ?1")
    IncorrectLogin findByip(String ipAddress);
}
