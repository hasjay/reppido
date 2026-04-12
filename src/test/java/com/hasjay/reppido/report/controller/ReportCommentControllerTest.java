package com.hasjay.reppido.report.controller;

import com.hasjay.reppido.exception.ReportNotFoundException;
import com.hasjay.reppido.report.dto.CreateReportCommentRequest;
import com.hasjay.reppido.report.dto.ReportCommentResponse;
import com.hasjay.reppido.report.service.ReportCommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportCommentController.class)
class ReportCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportCommentService reportCommentService;

    @Test
    void testAddComment() throws Exception {
        CreateReportCommentRequest request = new CreateReportCommentRequest("This is a test comment");

        ReportCommentResponse response = ReportCommentResponse.builder()
                .id(1)
                .reportId(1)
                .comment("This is a test comment")
                .createdDate(LocalDateTime.now())
                .build();

        when(reportCommentService.addComment(eq(1), any(CreateReportCommentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/reports/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reportId").value(1))
                .andExpect(jsonPath("$.comment").value("This is a test comment"));
    }

    @Test
    void testAddCommentBlankComment() throws Exception {
        CreateReportCommentRequest request = new CreateReportCommentRequest("");

        mockMvc.perform(post("/reports/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.comment").exists());
    }

    @Test
    void testAddCommentMissingBody() throws Exception {
        mockMvc.perform(post("/reports/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.comment").exists());
    }

    @Test
    void testAddCommentReportNotFound() throws Exception {
        CreateReportCommentRequest request = new CreateReportCommentRequest("Some comment");

        when(reportCommentService.addComment(eq(9999), any(CreateReportCommentRequest.class)))
                .thenThrow(new ReportNotFoundException(9999));

        mockMvc.perform(post("/reports/9999/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetComments() throws Exception {
        List<ReportCommentResponse> responses = List.of(
                ReportCommentResponse.builder().id(1).reportId(1).comment("First comment").createdDate(LocalDateTime.now()).build(),
                ReportCommentResponse.builder().id(2).reportId(1).comment("Second comment").createdDate(LocalDateTime.now()).build()
        );

        when(reportCommentService.getCommentsByReport(1)).thenReturn(responses);

        mockMvc.perform(get("/reports/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].comment").value("First comment"))
                .andExpect(jsonPath("$[1].comment").value("Second comment"));
    }

    @Test
    void testGetCommentsReportNotFound() throws Exception {
        when(reportCommentService.getCommentsByReport(9999))
                .thenThrow(new ReportNotFoundException(9999));

        mockMvc.perform(get("/reports/9999/comments"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
