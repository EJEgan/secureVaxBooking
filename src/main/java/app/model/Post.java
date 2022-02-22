package app.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue
    private Long post_id;

    @ManyToOne
    private User user;

    @NotBlank
    private String post_title;

    @NotBlank
    private String post_content;

    public Post() {
        super();
    }

    public Post(User user, String post_title, String post_content) {
        this.user = user;
        this.post_title = post_title;
        this.post_content = post_content;
    }

    public Long getPost_id() {
        return post_id;
    }

    public void setPost_id(Long post_id) {
        this.post_id = post_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user_id) {
        this.user = user_id;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }
}