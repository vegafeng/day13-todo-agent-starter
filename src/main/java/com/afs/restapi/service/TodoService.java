package com.afs.restapi.service;

import com.afs.restapi.entity.Todo;
import com.afs.restapi.exception.TodoNotFoundException;
import com.afs.restapi.repository.TodoRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;


@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Tool(description = "Get all todos")
    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    @Tool(description = "Create new todo. cannot set id, if set id, will be failed")
    public Todo create(Todo todo) {
        return todoRepository.save(todo);
    }

    @Tool(description = "Update todo by id. Should set id, if not call findAll first.")
    public Todo updateById(Integer id, Todo newTodo) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        if (newTodo.getDone() != null) {
            todo.setDone(newTodo.getDone());
        }
        if (newTodo.getText() != null) {
            todo.setText(newTodo.getText());
        }
        return todoRepository.save(todo);
    }

    @Tool(description = "Delete todo by id")
    public void deleteById(Integer id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
     }
// // to get todo by text
//     @Tool(description = "Get todo by text")
//     public List<Todo> findByText(String text) {
//         return todoRepository.findByTextContainingIgnoreCase(text);
//     }

//     @Tool(description = "Get the content of the README.md file")
//     public String getReadmeContent() throws IOException {
//         ClassPathResource resource = new ClassPathResource("README.md");
//         return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
//     }   
//     // to get todo by people owning it
//     @Tool(description = "Get todo by owner")
//     public List<Todo> findByOwner(String owner) {
//         return todoRepository.findByOwnerContainingIgnoreCase(owner);
//     }

//     // to delete all my data
//     @Tool(description = "Delete all todos by owner")
//     public void deleteByOwner(String owner) {
//         todoRepository.deleteByOwnerContainingIgnoreCase(owner);
//     }

//     // delete all data from database
//     @Tool(description = "Delete all todos")
//     public void deleteAll() {
//         todoRepository.deleteAll();
//     }
}
