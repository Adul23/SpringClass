package com.example.spring_class.web;

import com.example.spring_class.task.application.service.TaskService;
import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.domain.model.TaskStatus;
import com.example.spring_class.exceptions.ResourceNotFoundException;
import com.example.spring_class.task.web.TaskController;
import com.example.spring_class.task.web.error.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    TaskService service;

    @Test
    void createsTaskWithLocationHeader() throws Exception {
        when(service.create(any(Task.class))).thenReturn(
                new Task(10L, "Learn Java", null, TaskStatus.TODO, null)
        );

        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Learn Java",
                                  "status": "TODO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tasks/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Learn Java"));
    }

    @Test
    void rejectsBlankTitle() throws Exception {
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "status": "TODO"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.title").isArray());

        verifyNoInteractions(service);
    }

    @Test
    void returnsUnifiedNotFoundError() throws Exception {
        when(service.findById(99L))
                .thenThrow(new ResourceNotFoundException(
                        "Task 99 was not found"
                ));

        mvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Task 99 was not found"))
                .andExpect(jsonPath("$.path").value("/api/tasks/99"));
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deletesTask() throws Exception {
        mvc.perform(delete("/api/tasks/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).delete(10L);
    }

    @Test
    void limitsPageSizeAndAddsIdSort() throws Exception {
        when(service.findAll(any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable page = invocation.getArgument(0);
                    return new PageImpl<Task>(List.of(), page, 0);
                });

        mvc.perform(get("/api/tasks")
                        .param("size", "1000")
                        .param("sort", "title,desc"))
                .andExpect(status().isOk());

        var captor = org.mockito.ArgumentCaptor
                .forClass(Pageable.class);

        verify(service).findAll(captor.capture());

        Pageable actual = captor.getValue();

        org.junit.jupiter.api.Assertions.assertEquals(
                100, actual.getPageSize()
        );
        org.junit.jupiter.api.Assertions.assertNotNull(
                actual.getSort().getOrderFor("id")
        );
        org.junit.jupiter.api.Assertions.assertTrue(
                actual.getSort().getOrderFor("title").isDescending()
        );
    }
}