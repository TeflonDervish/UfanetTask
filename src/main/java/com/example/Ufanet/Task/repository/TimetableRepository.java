package com.example.Ufanet.Task.repository;

import com.example.Ufanet.Task.model.Timetable;
import com.example.Ufanet.Task.model.dto.TimeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {


    @Query("""
                select
                    to_char(dateTime, 'HH24:MI:SS') as time,
                    count(*) as count
                from
                    Timetable
                where
                     date(dateTime) = date(:dateTime)
                group by
                     to_char(dateTime, 'HH24:MI:SS')
            """)
    List<TimeCount> findByDate(@Param("dateTime") LocalDateTime dateTime);

    @Query("""
                select count(*)
                from Timetable
                where dateTime = :date
            """)
    Long countByDate(@Param("date") LocalDateTime dateTime);


}
