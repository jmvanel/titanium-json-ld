package com.apicatalog.jsonld;

import static org.junit.Assume.assumeFalse;

import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.apicatalog.jsonld.api.JsonLdError;
import com.apicatalog.jsonld.document.RemoteDocument;
import com.apicatalog.jsonld.lang.Version;
import com.apicatalog.jsonld.loader.LoadDocumentOptions;
import com.apicatalog.jsonld.suite.JsonLdManifestLoader;
import com.apicatalog.jsonld.suite.JsonLdTestCase;
import com.apicatalog.jsonld.suite.JsonLdTestRunnerJunit;

@RunWith(Parameterized.class)
public class FlattenTest {

    @Parameterized.Parameter(0)
    public JsonLdTestCase testCase;

    @Parameterized.Parameter(1)
    public String testId;
    
    @Parameterized.Parameter(2)
    public String testName;
        
    @Parameterized.Parameter(3)
    public String baseUri;
    
    @Test
    public void testFlatten() {

        // skip specVersion == 1.0
        assumeFalse(Version.V1_0.equals(testCase.options.specVersion));
        
        try {
            (new JsonLdTestRunnerJunit(testCase)).execute(options -> {
                
                RemoteDocument jsonContext = null;
                
                //pre-load context
                if (testCase.context != null) {
                    jsonContext = options.getDocumentLoader().loadDocument(testCase.context, new LoadDocumentOptions());
                    
                    Assert.assertNotNull(jsonContext);
                    Assert.assertTrue(jsonContext.getJsonContent().isPresent());
                }
                                
                return JsonLd
                        .flatten(testCase.input) 
                        .context(jsonContext != null ?  jsonContext.getJsonContent().get() : null)
                        .options(options)
                        .get();

            });
            
        } catch (JsonLdError e) {
            Assert.fail(e.getMessage());
        }            
    }

    @Parameterized.Parameters(name = "{1}: {2}")
    public static Collection<Object[]> data() throws JsonLdError {        
        return JsonLdManifestLoader
                .load(JsonLdManifestLoader.JSON_LD_API_BASE, "flatten-manifest.jsonld")
                .stream()            
                .map(o -> new Object[] {o, o.id, o.name, o.baseUri})
                .collect(Collectors.toList());
    }
}
