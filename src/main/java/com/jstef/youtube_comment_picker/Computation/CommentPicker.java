package com.jstef.youtube_comment_picker.Computation;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.jstef.youtube_comment_picker.Exception.InvalidVideoUrl;
import com.jstef.youtube_comment_picker.Resource.Comment;
import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@PropertySource("classpath:application.properties")
public class CommentPicker {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${key}")
    private String apiKey;

    //if url is not valid, throw exception, return random comment otherwise
    public  Comment pickCommentOrThrow(String url){
        if(url==null || !(url.startsWith("https://www.youtube.com/watch?v="))){
            throw new InvalidVideoUrl("Url does not seem to be valid");
        }
        Map<Integer,Comment> map = getVideosFromUrl(url);
        Integer rand = (int)(Math.random()*map.size());
        Comment chosen = map.get(rand);
        return chosen;
    }

    //return all coments from a given video
    public  Map<Integer,Comment> getVideosFromUrl(String url){
        Map<Integer,Comment>map=new HashMap<>();

        //extract video id from url
        url=url.replace("https://www.youtube.com/watch?v=","");

        //as YouTube API lets to get at most 100 comments at once, there is do-while loop to get List of CommentThreads
        // each time. In every iteration the comments from snippets are put into map.
        try {
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), JSON_FACTORY,null)
                    .setApplicationName("youtube-cmdline-commentthreads-sample").build();
            CommentThreadListResponse response = null;
            response = youtube.commentThreads()
                    .list("snippet").setVideoId(url).setMaxResults(100l)
                    .setTextFormat("plainText").setKey(apiKey).execute();
            String token = null;
            do{
                //get list of comment threads after get from api
                List<CommentThread> commentThreads = response.getItems();
                for (CommentThread thread : commentThreads) {
                    //iterate through every comment, add author and message to map
                    CommentSnippet snippet = thread.getSnippet().getTopLevelComment().getSnippet();
                    String author = snippet.getAuthorDisplayName();
                    String message = snippet.getTextDisplay();
                    map.put(map.size(),new Comment(author,message));
                }
                //go to next result set using page token
                token=response.getNextPageToken();
                response=youtube.commentThreads().list("snippet").setPageToken(token).setVideoId(url).setMaxResults(100l)
                        .setTextFormat("plainText").setKey(apiKey).execute();
            }while(token!=null);

        }  catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
        return map;
    }
}
