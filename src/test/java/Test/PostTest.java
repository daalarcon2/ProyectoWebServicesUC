package Test;
import General.BaseTest;
import helpers.DataHelper;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import model.Post;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import specifications.RequestSpecs;
import specifications.ResponseSpecs;
import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.XmlPath.CompatibilityMode.HTML;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

public class PostTest extends BaseTest {

    private static String resourcePath= "/v1/post";
    private static Integer postCreated =0 ;
    private static Integer postCreated2 =0 ;

    @BeforeGroups("create_post")
    public void createPostforDelete(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        Response response = given()
                .spec(RequestSpecs.generateToken())
                .body(testPost)
                .post(resourcePath);

        JsonPath jsonPathEvaluator = response.jsonPath();
        postCreated= jsonPathEvaluator.get("id");
        System.out.println(postCreated.toString()+ "numero de post");
    }

    @Test
    public void createPostSucess(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        Response response4= given()
                .spec(RequestSpecs.generateToken())
                .body(testPost)
                .post(resourcePath);

        JsonPath jsonPathEvaluator = response4.jsonPath();
        postCreated2= jsonPathEvaluator.get("id");
        System.out.println(postCreated2.toString()+ "numero de articulo");
    }

    @Test(groups = "create_post")
    public void getOneArticle(){

        Response response2 = given().contentType(ContentType.JSON)
                .spec(RequestSpecs.generateToken())
                .when()
                .get(resourcePath+"/"+postCreated2.toString());
        int statusCode = response2.getStatusCode();
        assertThat(statusCode,equalTo(200));

        JsonPath jsonPath = new JsonPath(response2.asString());
        String test = (jsonPath.getString("data"));
    }

    @Test

    public void getAllArticlesFromUser(){

        Response response3 = given().contentType(ContentType.JSON)
                .spec(RequestSpecs.generateToken())
                .when()
                .get(resourcePath+"s");
        int statusCode = response3.getStatusCode();
        assertThat(statusCode,equalTo(200));

    }

    @Test(groups = "create_post")
    public void deleteArticleSuccess(){

        given()
                .spec(RequestSpecs.generateToken())
                .delete(resourcePath + "/" + postCreated.toString())
                .then()
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test
    public void editPostSucess(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

       given()
                .spec(RequestSpecs.generateToken())
                .body(testPost)
                .put(resourcePath+"/"+postCreated2.toString())
               .then()
               .body("message", equalTo("Post updated"))
               .statusCode(200)
               .spec(ResponseSpecs.defaultSpec());

        System.out.println(postCreated2+" edit");

    }

    @Test
    public void invalidTokenCantCreateNewPost(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateFakeToken())
                .body(testPost)
                .post(resourcePath+ "/v1/post")
                .then()
                .statusCode(404);

    }

    @Test
    public void AinvalidEditionPost(){

        Post testPost = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        Response response1= given()
                .spec(RequestSpecs.generateFakeToken())
                .when()
                .put(resourcePath+ "/v1/post").then().contentType(ContentType.HTML).extract()
                .response();
        assertEquals(response1.getStatusCode(), 404);
        XmlPath htmlPath = new XmlPath(HTML, response1.getBody().asString());
        assertEquals(htmlPath.getString("html.head.title"), "404 Page");

    }
}

