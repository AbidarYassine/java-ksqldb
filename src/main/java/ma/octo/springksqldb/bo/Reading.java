package ma.octo.springksqldb.bo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Reading {
    private String id;
    private String timestamp;
    private int reading;
}
