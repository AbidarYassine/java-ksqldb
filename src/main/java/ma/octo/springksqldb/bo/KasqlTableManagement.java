package ma.octo.springksqldb.bo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class KasqlTableManagement implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String fileName;
    private String fileContent;
    private String version;
    private String hashFile;
}
