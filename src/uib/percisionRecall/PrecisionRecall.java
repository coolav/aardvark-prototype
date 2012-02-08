package uib.percisionRecall;


import org.apache.lucene.benchmark.quality.trec.*;

import org.apache.lucene.store.*;
import org.apache.lucene.search.*;
import java.io.*;
import java.util.Map;
import org.apache.lucene.benchmark.quality.Judge;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.QualityQueryParser;
import org.apache.lucene.benchmark.quality.utils.DocNameExtractor;
import org.apache.lucene.benchmark.quality.utils.SimpleQQParser;
import org.apache.lucene.benchmark.quality.utils.SubmissionReport;

public class PrecisionRecall {

    public static void main(String[] args) throws Throwable, org.apache.lucene.queryParser.ParseException {

        File topicsFile = new File("C:/Users/Olav/Documents/NetBeansProjects/Aardvark/benchmark/test/surftopics.txt");
        File qrelsFile = new File("C:/Users/Olav/Documents/NetBeansProjects/Aardvark/benchmark/test/surfiosen.txt");
        Directory dir = FSDirectory.open(new File("C:/Users/Olav/Documents/NetBeansProjects/Aardvark/index"));
        Searcher searcher = new IndexSearcher(dir, true);

        String docNameField = "filename";

         PrintWriter logger = new PrintWriter(System.out, true);
         DocNameExtractor extractor = new DocNameExtractor(docNameField);

         for(int i = 0; i < searcher.maxDoc(); i++){
            String  doc  = extractor.docName(searcher, i);
            logger.println(doc);
         }


        TrecTopicsReader qReader = new TrecTopicsReader();   //#1

        QualityQuery qqs[] = qReader.readQueries(            //#1
            new BufferedReader(new FileReader(topicsFile))); //#1

        //int l = qqs.length;
        //logger.println(l);
        Judge judge = new TrecJudge(new BufferedReader(      //#2
            new FileReader(qrelsFile)));                     //#2

        //judge.validateData(qqs, logger);                     //#3

        QualityQueryParser qqParser = new SimpleQQParser("title", "contents");  //#4

        QualityBenchmark qrun = new QualityBenchmark(qqs, qqParser, searcher, docNameField);
      
        SubmissionReport submitLog = null;
        QualityStats stats[] = qrun.execute(judge,           //#5
            submitLog, logger);

        QualityStats avg = QualityStats.average(stats);      //#6
        avg.log("SUMMARY",2,logger, "  ");
        dir.close();

    }
}

/*
#1 Read TREC topics as QualityQuery[]
#2 Create Judge from TREC Qrel file
#3 Verify query and Judge match
#4 Create parser to translate queries into Lucene queries
#5 Run benchmark
#6 Print precision and recall measures
 */
