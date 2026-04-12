package com.hasjay.reppido.report.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rp_report_comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportComment {

    @Id
    @Column(name = "rpco_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_comment_seq")
    @SequenceGenerator(name = "report_comment_seq", sequenceName = "seq_rpco_id", allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rpco_report_id", nullable = false)
    private Report report;

    @Column(name = "rpco_comment", nullable = false)
    private String comment;

    @Column(name = "rpco_user")
    private String user;

    @Column(name = "rpco_create_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
}
