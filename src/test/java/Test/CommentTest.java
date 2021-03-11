package Test;
import General.BaseTest;
import helpers.DataHelper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.Comment;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import specifications.RequestSpecs;
import model.Post;
import specifications.ResponseSpecs;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

public class CommentTest extends BaseTest {

    private static String resourcePathComment= "/v1/comment";
    private static String resourcePathPost= "/v1/post";
    private static Integer postCreated = 0;
    private static Integer commentCreated =0 ;
    private static Integer commentCreated2 =0 ;


    @BeforeGroups("create_post")
    public void AcreatePost() {
        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());
        Response response = given()
                .spec(RequestSpecs.generateToken())
                .body(testPost)
                .post(resourcePathPost);

        JsonPath jsonPathEvaluator = response.jsonPath();
        postCreated = jsonPathEvaluator.get("id");
        System.out.println(postCreated.toString()+ "numero de post");
    }

    @Test(groups="create_post")
    public void BcreateComment(){

        Comment testComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());


        Response responseA= given()
                .auth()
                .basic("testuser","testpass")
                .body(testComment)
                .when()
                .post(resourcePathComment+"/"+postCreated.toString());

        JsonPath jsonPathEvaluator = responseA.jsonPath();
        commentCreated= jsonPathEvaluator.get("id");
        System.out.println(commentCreated+ "numero de comentario");

    }

    @BeforeGroups("create_comment")
    public void CcreateCommentSucess(){

        Comment testPost = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        Response responseB= given()
                .auth()
                .basic("testuser","testpass")
                .body(testPost)
                .when()
                .post(resourcePathComment+"/"+postCreated.toString());

        JsonPath jsonPathEvaluator = responseB.jsonPath();
        commentCreated2= jsonPathEvaluator.get("id");
        System.out.println(commentCreated2+ "segundo comentario");
    }

    @Test
    public void DgetCommentsFromUser(){

        Response responseC = given()
                .auth()
                .basic("testuser","testpass")
                .when()
                .get(resourcePathComment+"s/"+postCreated);
        int statusCode = responseC.getStatusCode();
        assertThat(statusCode, Matchers.equalTo(200));

        JsonPath jsonPath = new JsonPath(responseC.asString());
        String test = (jsonPath.getString("results"));
    }

    @Test
    public void FgetOneComment(){

        Response responseD = given()
                .auth()
                .basic("testuser","testpass")
                .when()
                .get(resourcePathComment+"/"+postCreated+"/"+commentCreated);
        int statusCode = responseD.getStatusCode();
        assertThat(statusCode, Matchers.equalTo(200));
    }

    @Test(groups = "create_post")
    public void deleteArticleSuccess(){

        given()
                .auth()
                .basic("testuser","testpass")
                .delete(resourcePathComment+"/"+postCreated+"/"+commentCreated)
                .then()
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test
    public void CeditArticleSucess(){

        Comment testComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given()
                .auth()
                .basic("testuser","testpass")
                .body(testComment)
                .put(resourcePathComment+"/"+postCreated+"/"+commentCreated.toString())
                .then()
                .body("message", Matchers.equalTo("Comment updated"))
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
        System.out.println(postCreated+" edit");
    }

    @Test
    public void invalidUsertoCreate(){

        Comment testComment = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given()
                .auth()
                .basic("testusers","testpass")
                .body(testComment)
                .post(resourcePathComment+"/"+postCreated)
                .then()
                .body("message", Matchers.equalTo("Please login first"))
                .statusCode(401);
    }
}