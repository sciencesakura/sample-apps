package com.sciencesakura.sample.api;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sciencesakura.sample.domain.task.Priority;
import com.sciencesakura.sample.domain.task.Task;
import com.sciencesakura.sample.domain.task.TaskQuery;
import com.sciencesakura.sample.domain.task.TaskService;
import com.sciencesakura.sample.domain.task.TaskStatus;
import com.sciencesakura.sample.domain.user.UserInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  TaskService taskService;

  @Nested
  @WithMockUser(roles = "USER")
  class GET_tasks {

    @Test
    void return_200_OK() throws Exception {
      var stubArg = new TaskQuery();
      stubArg.setText("text");
      stubArg.setAssignee("assignee@example.com");
      stubArg.setStatus(Set.of(TaskStatus.CREATED, TaskStatus.WORK_IN_PROGRESS));
      stubArg.setPage(2);
      stubArg.setSize(30);
      stubArg.setOrder(List.of(Order.by("title"), Order.by("dueDate")));
      var assignee = new UserInfo();
      assignee.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      var stubReturn = new Task();
      stubReturn.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      stubReturn.setTitle("title");
      stubReturn.setAssignee(assignee);
      stubReturn.setDueDate(LocalDate.parse("2099-01-01"));
      stubReturn.setPriority(Priority.HIGHER);
      stubReturn.setStatus(TaskStatus.WORK_IN_PROGRESS);
      stubReturn.setDescription("description");
      when(taskService.findAll(eq(stubArg))).thenReturn(new PageImpl<>(List.of(stubReturn)));

      mockMvc.perform(get("/tasks")
              .param("text", "text")
              .param("assignee", "assignee@example.com")
              .param("status", "0", "1")
              .param("page", "2")
              .param("size", "30")
              .param("order", "title", "dueDate"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json("""
              [
                {
                  "id": "00000000-0000-0000-0000-000000000001",
                  "title": "title",
                  "assignee": {
                    "id": "00000000-0000-0000-0000-000000000001"
                  },
                  "dueDate": "2099-01-01",
                  "priority": 10,
                  "status": 1,
                  "description": "description"
                }
              ]
              """));
    }
  }

  @Nested
  @WithMockUser(roles = "USER")
  class GET_tasks_id {

    @Test
    void return_200_OK() throws Exception {
      var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
      var assignee = new UserInfo();
      assignee.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      var stubReturn = new Task();
      stubReturn.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      stubReturn.setTitle("title");
      stubReturn.setAssignee(assignee);
      stubReturn.setDueDate(LocalDate.parse("2099-01-01"));
      stubReturn.setPriority(Priority.HIGHER);
      stubReturn.setStatus(TaskStatus.WORK_IN_PROGRESS);
      stubReturn.setDescription("description");
      when(taskService.findById(eq(id))).thenReturn(Optional.of(stubReturn));

      mockMvc.perform(get("/tasks/{id}", id))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json("""
              {
                "id": "00000000-0000-0000-0000-000000000001",
                "title": "title",
                "assignee": {
                  "id": "00000000-0000-0000-0000-000000000001"
                },
                "dueDate": "2099-01-01",
                "priority": 10,
                "status": 1,
                "description": "description"
              }
              """));
    }

    @Test
    void return_404_NOT_FOUND() throws Exception {
      var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
      when(taskService.findById(eq(id))).thenReturn(Optional.empty());

      mockMvc.perform(get("/tasks/{id}", id))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.messages", hasSize(1)))
          .andExpect(jsonPath("$.messages[0].message").isString())
          .andExpect(jsonPath("$.messages[0].details.resource").value("Task"))
          .andExpect(jsonPath("$.messages[0].details.identifier").value("00000000-0000-0000-0000-000000000001"));
    }
  }

  @Nested
  @WithMockUser(roles = "USER")
  class POST_tasks {

    @Test
    void return_201_CREATED() throws Exception {
      var assignee = new UserInfo();
      assignee.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      var stubArg = new Task();
      stubArg.setTitle("title");
      stubArg.setAssignee(assignee);
      stubArg.setDueDate(LocalDate.parse("2099-01-01"));
      stubArg.setPriority(Priority.HIGHER);
      stubArg.setStatus(TaskStatus.WORK_IN_PROGRESS);
      stubArg.setDescription("description");
      var stubReturn = stubArg.clone();
      stubReturn.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      when(taskService.create(eq(stubArg))).thenReturn(stubReturn);

      mockMvc.perform(post("/tasks")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "title": "title",
                    "assignee": {
                      "id": "00000000-0000-0000-0000-000000000001"
                    },
                    "dueDate": "2099-01-01",
                    "priority": 10,
                    "status": 1,
                    "description": "description"
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location", endsWith("/tasks/00000000-0000-0000-0000-000000000001")));
    }
  }

  @Nested
  @WithMockUser(roles = "USER")
  class PUT_tasks_id {

    @Test
    void return_204_NO_CONTENT() throws Exception {
      var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
      var assignee = new UserInfo();
      assignee.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
      var stubArg = new Task();
      stubArg.setId(id);
      stubArg.setTitle("title");
      stubArg.setAssignee(assignee);
      stubArg.setDueDate(LocalDate.parse("2099-01-01"));
      stubArg.setPriority(Priority.HIGHER);
      stubArg.setStatus(TaskStatus.WORK_IN_PROGRESS);
      stubArg.setDescription("description");
      var stubReturn = stubArg.clone();
      when(taskService.update(eq(id), eq(stubArg))).thenReturn(stubReturn);

      mockMvc.perform(put("/tasks/{id}", id)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "id": "00000000-0000-0000-0000-000000000001",
                    "title": "title",
                    "assignee": {
                      "id": "00000000-0000-0000-0000-000000000001"
                    },
                    "dueDate": "2099-01-01",
                    "priority": 10,
                    "status": 1,
                    "description": "description"
                  }
                  """))
          .andExpect(status().isNoContent());
    }
  }

  @Nested
  @WithMockUser(roles = "USER")
  class DELETE_tasks_id {

    @Test
    void return_204_NO_CONTENT() throws Exception {
      var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
      doNothing().when(taskService).delete(eq(id));

      mockMvc.perform(delete("/tasks/{id}", id))
          .andExpect(status().isNoContent());
    }
  }
}
