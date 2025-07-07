package ru.otus.dao;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.crm.model.Client;

public interface ClientRepository extends ListCrudRepository<Client, Long> {
    @Query(
            value =
                    """
                select  c.id as client_id,
                        c.name as name,
                        p.id as phone_id,
                        p.number as phone_number,
                        a.street as address_street
                        from client c
                            left join phone p on c.id = p.client_id
                            left join address a on c.id = a.client_id
                        order by c.id
            """,
            resultSetExtractorClass = ClientResultSetExtractor.class)
    List<Client> findAllCustomRequest();

    @Query(
            value =
                    """
            SELECT c.id as client_id,
                   c.name as name,
                   a.street as address_street,
                   p.id as phone_id,
                   p.number as phone_number
              FROM client c
         LEFT JOIN address a ON c.id = a.client_id
         LEFT JOIN phone p ON c.id = p.client_id
             WHERE c.id = :id
          ORDER BY c.id
        """,
            resultSetExtractorClass = ClientResultSetExtractor.class)
    List<Client> findByIdCustomMapping(@Param("id") Long id);
}
