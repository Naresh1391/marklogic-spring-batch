package com.marklogic.spring.batch.config;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.spring.batch.item.MarkLogicItemWriter;
import com.marklogic.spring.batch.item.processor.ResourceToDocumentWriteOperationItemProcessor;
import com.marklogic.uri.DefaultUriGenerator;
import com.marklogic.uri.UriGenerator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import com.marklogic.spring.batch.item.reader.EnhancedResourcesItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import org.springframework.util.Assert;

@EnableBatchProcessing
@Import( { MarkLogicBatchConfigurer.class } )
public class ImportDocumentsFromDirectoryConfig  {
    
    @Bean
    public Job importDocumentsFromDirectoryJob(
        JobBuilderFactory jobBuilderFactory,
        @Qualifier("importDocumentsFromDirectoryStep1") Step step) {
        return jobBuilderFactory.get("importDocumentsFromDirectoryJob").start(step).build();
    }

    @Bean
    @JobScope
    public Step importDocumentsFromDirectoryStep1(
            StepBuilderFactory stepBuilderFactory,
            DatabaseClientProvider databaseClientProvider,
            @Value("#{jobParameters['input_file_path']}") String inputFilePath,
            @Value("#{jobParameters['input_file_pattern']}") String inputFilePattern,
            @Value("#{jobParameters['document_type']}") String documentType,
            @Value("#{jobParameters['output_uri_prefix']}") String outputUriPrefix,
            @Value("#{jobParameters['output_uri_replace']}") String outputUriReplace,
            @Value("#{jobParameters['output_uri_suffix']}") String outputUriSuffix,
            @Value("#{jobParameters['output_collections']}") String outputCollections) {

        Assert.hasText(inputFilePath, "input_file_path cannot be null");
    
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        if (outputCollections != null) {
            metadata.withCollections(outputCollections.split(","));
        }
        ResourceToDocumentWriteOperationItemProcessor processor = new ResourceToDocumentWriteOperationItemProcessor();
        if (documentType != null) {
            processor.setFormat(Format.valueOf(documentType.toUpperCase()));
        }
        processor.setMetadataHandle(metadata);
        
        MarkLogicItemWriter itemWriter = new MarkLogicItemWriter(databaseClientProvider.getDatabaseClient());
        itemWriter.setOutputUriPrefix(outputUriPrefix);
        itemWriter.setOutputUriReplace(outputUriReplace);
        itemWriter.setOutputUriSuffix(outputUriSuffix);
        
        return stepBuilderFactory.get("step")
                .<Resource, DocumentWriteOperation>chunk(10)
                .reader(new EnhancedResourcesItemReader(inputFilePath, inputFilePattern))
                .processor(processor)
                .writer(itemWriter)
                .build();
    }

    public UriGenerator<String> uriGenerator(String outputUriPrefix, String outputUriReplace, String outputUriSuffix) {
        DefaultUriGenerator uriGen = new DefaultUriGenerator();
        uriGen.setOutputUriPrefix(outputUriPrefix);
        uriGen.setOutputUriReplace(outputUriReplace);
        uriGen.setOutputUriSuffix(outputUriSuffix);
        return uriGen;
    }
}
