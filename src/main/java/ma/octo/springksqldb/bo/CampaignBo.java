package ma.octo.springksqldb.bo;

import lombok.Getter;
import lombok.Setter;
import ma.octo.springksqldb.aspect.annotations.MaterializedView;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "campaigns")
@MaterializedView
public class CampaignBo implements Serializable {

    @Id
    private Long id;
    private String name;
    @Column(name = "STATUT_COURANT")
    @Enumerated(EnumType.ORDINAL)
    private CampaignStatus currentStatus;

    @ManyToOne
    @JoinColumn(name = "user_bo_id")
    private UserBo userBo;

}
