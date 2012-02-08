/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.percisionRecall;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.benchmark.quality.*;
import org.apache.lucene.benchmark.quality.utils.*;
import org.apache.lucene.benchmark.quality.trec.*;

/**
 *
 * @author Olav
 */
public class PercisionRecallTest {

    public static void main(String[] args) throws Throwable {

        File topicsFile = new File("C:/Users/Olav/Documents/NetBeansProjects/Aardvark/output/topics.txt");
        File qrelsFile = new File("C:/Users/Olav/Documents/NetBeansProjects/Aardvark/output/qrels.txt");
        Directory dir = FSDirectory.open(new File("C:/Users/Olav/Documents/NetBeansProjects/Aardvark/index/"));
        Searcher searcher = new IndexSearcher(dir, true);

        String docNameField = "E:/Bileter/Nokian95/20080528/28052008047.jpg";

        PrintWriter logger = new PrintWriter(System.out, true);

        TrecTopicsReader qReader = new TrecTopicsReader();   //#1
        QualityQuery qqs[] = qReader.readQueries( //#1
                new BufferedReader(new FileReader(topicsFile))); //#1

        Judge judge = new TrecJudge(new BufferedReader( //#2
                new FileReader(qrelsFile)));                     //#2

        judge.validateData(qqs, logger);                     //#3

        QualityQueryParser qqParser = new SimpleQQParser("title", "contents");  //#4

        QualityBenchmark qrun = new QualityBenchmark(qqs, qqParser, searcher, docNameField);
        SubmissionReport submitLog = null;
        QualityStats stats[] = qrun.execute(judge, //#5
                submitLog, logger);

        QualityStats avg = QualityStats.average(stats);      //#6
        avg.log("SUMMARY", 2, logger, "  ");
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
