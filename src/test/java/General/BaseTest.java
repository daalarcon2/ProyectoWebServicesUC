package General;
import io.restassured.RestAssured;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

public class BaseTest {

    protected ResponseSpecification headerSpec;

    protected static String baseUrl;

    @Parameters("baseUrl")
    @BeforeClass
    public void setUp(String baseUrl){

        RestAssured.baseURI= baseUrl;

    }

}
