package ma.octo.springksqldb.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MaterializeView {
    private String name;
    private String query;
}
