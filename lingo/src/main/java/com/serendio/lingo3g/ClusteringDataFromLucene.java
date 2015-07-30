
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.serendio.lingo3g;

//import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
//import org.apache.lucene.store.FSDirectory;
//import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingComponentConfiguration;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.source.lucene.LuceneDocumentSource;
import org.carrot2.source.solr.SolrDocumentSource;
import org.carrot2.source.solr.SolrDocumentSourceDescriptor;

import com.carrotsearch.lingo3g.Lingo3GClusteringAlgorithm;
import com.google.common.collect.Maps;
//import java.nio.file.Paths;
//import com.serendio.lingo3g.ConsoleFormatter;
//import org.carrot2.source.lucene.LuceneDocumentSourceDescriptor;

/**
 * This example shows how to cluster {@link Document}s retrieved from a Lucene index using
 * the {@link LuceneDocumentSource}.
 * <p>
 * It is assumed that you are familiar with {@link ClusteringDocumentList} and
 * {@link UsingCachingController} examples.
 * 
 * @see CreateLuceneIndex
 * @see ClusteringDataFromLuceneWithCustomFields
 * @see ClusteringDocumentList
 * @see UsingCachingController
 */
public class ClusteringDataFromLucene
{
    public static void main(String [] args) throws IOException
    {
        /*
         * We will use the CachingController for this example. Running
         * LuceneDocumentSource within the CachingController will let us open the index
         * once per component initialization and not once per query, which would be the
         * case with SimpleController. We will also use this opportunity to show how
         * component-specific attribute values can be passed during CachingComponent
         * initialization.
         */

        /*
         * Create a caching controller that will reuse processing component instances, but
         * will not perform any caching of results produced by components. We will leave
         * caching of documents from Lucene index to Lucene and the operating system
         * caches.
         */
        final Controller controller = ControllerFactory.createPooling();

        /*
         * Prepare a map with component-specific attributes. Here, this map will contain
         * the index location and names of fields to be used to fetch document title and
         * summary.
         */
        final Map<String, Object> luceneGlobalAttributes = new HashMap<String, Object>();

        //String indexPath = "/home/serendio/amc_dev/data/index";
        String serviceUrlBase = "Your solr source Url";
//        if (args.length == 1)
//        {
//        	serviceUrlBase = args[0];
//        }
        SolrDocumentSourceDescriptor.attributeBuilder(luceneGlobalAttributes)
        .serviceUrlBase(serviceUrlBase);       
        //LuceneDocumentSourceDescriptor
            //.attributeBuilder(luceneGlobalAttributes)
            //.directory(FSDirectory.open(Paths.get(indexPath)));
            //.directory(FSDirectory.open(new File(indexPath)));

        /*
         * Specify fields providing data inside your Lucene index.
         */
        
        SolrDocumentSourceDescriptor 
        .attributeBuilder(luceneGlobalAttributes)
        	.solrIdFieldName("id")
        	.solrTitleFieldName("title")
            .solrSummaryFieldName("sentence")
            .solrUrlFieldName("url")
            .readClusters(true)
            .useHighlighterOutput(true)
            .copyFields(true)
            .start(0)
            .results(5000);
            
//        SimpleFieldMapperDescriptor
//            .attributeBuilder(luceneGlobalAttributes)
//            .titleField("id")
//            .contentField("sentence")
//            .titleField("title")            
//            .urlField("url")
//            .searchFields(Arrays.asList(new String [] {"id","title", "sentence","url"}));
            //.contentField("title")
            //.urlField("url")            
            //.contentField("content");            
//           .searchFields(Arrays.asList(new String [] {"sentence","sentence"}));

        /*
         * Initialize the controller passing the above attributes as component-specific
         * for Lucene. The global attributes map will be empty. Note that we've provided
         * an identifier for our specially-configured Lucene component, we'll need to use
         * this identifier when performing processing.
         */
        controller.init(new HashMap<String, Object>(),
        		new ProcessingComponentConfiguration(SolrDocumentSource.class, "lucene", luceneGlobalAttributes));            
        
        /*
         * Perform processing.
         */
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        String query = "case_id:"+input;
        final Map<String, Object> processingAttributes = Maps.newHashMap();
        CommonAttributesDescriptor.attributeBuilder(processingAttributes)
            .query(query);

        /*
         * We need to refer to the Lucene component by its identifier we set during
         * initialization. As we've not assigned any identifier to the
         * LingoClusteringAlgorithm we want to use, we can its fully qualified class name.
         */
        //ProcessingResult process = controller.process(processingAttributes, "lucene",
          //  LingoClusteringAlgorithm.class.getName());
//       ProcessingResult result = controller.process(processingAttributes,"lucene", Lingo3GClusteringAlgorithm.class);
//        //ProcessingResult result = controller.process(processingAttributes,"lucene",Lingo3GClusteringAlgorithm.class.getName());
//        
//        ConsoleFormatter.displayResults(result);
        ProcessingResult result = controller.process(processingAttributes,"lucene",Lingo3GClusteringAlgorithm.class.getName());
        //System.out.println(luceneGlobalAttributes);
        //System.out.println(processingAttributes);        
        
        //ConsoleFormatter.displayResults(process);
        
        Writer writer =new FileWriter("/home/ram/Desktop/Lingo"+input+".json");
        result.serializeJson(writer);
        
    }
}
