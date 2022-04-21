package ma.octo.springksqldb.bo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table
public class VersionKasqlQuery implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String version;
}
