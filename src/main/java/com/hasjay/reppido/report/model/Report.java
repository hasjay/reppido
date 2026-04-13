package com.hasjay.reppido.report.model;

import com.hasjay.reppido.category.model.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "rp_reports")
@NamedEntityGraph(
	    name = "Report.withCategotyAndComments",
	    attributeNodes = {
	        @NamedAttributeNode("comments"),
	        @NamedAttributeNode("category")
	    }
	)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

	@Id
	@Column(name = "repo_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_seq")
	@SequenceGenerator(name = "report_seq", sequenceName = "repo_id_seq", allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_category_id", nullable = false)
    private Category category;

    @Column(name = "repo_description")
    private String description;

    @Column(name = "repo_location", nullable = false)
    private String location;

    @Column(name = "repo_lon", nullable = false)
    private Double longitude;

    @Column(name = "repo_lat", nullable = false)
    private Double latitude;

    @Column(name = "repo_create_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;
    
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<ReportComment> comments;
}
