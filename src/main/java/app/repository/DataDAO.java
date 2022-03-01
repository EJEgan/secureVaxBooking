package app.repository;


import app.model.Data;
import app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DataDAO extends JpaRepository<Data,Integer> {

    @Query("SELECT Count(u) FROM Data u WHERE u.sex = ?1")
    public int getPopNo(String sex);

    @Query("SELECT Count(u) FROM Data u WHERE u.nationality = ?1")
    public int getByNationality(String nationality);

    @Query("SELECT Count(u) FROM Data u WHERE u.dob = ?1")
    public int getByDob(String age);

}
