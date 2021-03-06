package de.tudarmstadt.ukp.experiments.pythagoras.featureExtractors.length;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.experiments.pythagoras.annotations.TranscribedText;
import de.tudarmstadt.ukp.experiments.pythagoras.featureExtractorUtils.NGramUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import de.tudarmstadt.ukp.dkpro.tc.exception.TextClassificationException;

/**
 * Measures average lengths of utterances, sentences and words
 * 
 * @author Tahir Sousa
 * @version last updated: Apr 03, 2014 [Sousa]
 */
public class AverageLengthExtractor extends FeatureExtractorResource_ImplBase
    implements DocumentFeatureExtractor
{
	public static final String AV_CONVERSATION_LENGTH = "AverageConversationLength";
    public static final String AV_SENTENCE_LENGTH = "AverageSentenceLength";
    public static final String AV_WORD_LENGTH = "AverageWordLength";

    private static final String GROUP_PREFIX = "CSur_Length__";
    
    public List<Feature> extract(JCas jcas) 
    		throws TextClassificationException  {

        double sentenceLengthSum = 0.0;
        double studentSentenceLengthSum = 0.0;
        double teacherSentenceLengthSum = 0.0;
        
        double conversationLengthSum = 0.0;        
        double studentConversationLengthSum = 0.0;        
        double teacherConversationLengthSum = 0.0;
        
        double nrOfSentences = 0.0;
        double nrOfTeacherSentences = 0.0;
        double nrOfStudentSentences = 0.0;
        
        double nrOfConversations = 0.0;
        double nrOfTeacherConversations = 0.0;
        double nrOfStudentConversations = 0.0;
        
        double wordLengthSum = 0;
        double teacherWordLengthSum = 0;
        double studentWordLengthSum = 0;
        
        double nrOfWords = 0.0;
        double nrOfTeacherWords = 0.0;
        double nrOfStudentWords = 0.0;

        for	(TranscribedText tt : JCasUtil.select(jcas, TranscribedText.class))	{
        	if	(tt.getSpeaker().contains("T"))	{
        		int sentencesInConversation = 0;
        		for (Sentence sent : JCasUtil.selectCovered(jcas, Sentence.class, tt)) {
                    int wordsInSentence = 0;
                    
                    List<String> tokens = JCasUtil.toText(JCasUtil.selectCovered(Token.class, sent));
                    List<String> cleanedTokens = NGramUtils.removeSpecialCharacters(tokens);
                    
                    for (String t : cleanedTokens) {
                        int tokenLength = t.length();
                        if	(tokenLength > 0)	{
                        	nrOfTeacherWords++;
                            nrOfWords++;
                            wordsInSentence++;
                            teacherWordLengthSum += tokenLength;
                            wordLengthSum += tokenLength;	
                        }
                        
                    }
                    teacherSentenceLengthSum += wordsInSentence;
                    sentenceLengthSum += wordsInSentence;
                    nrOfTeacherSentences++;
                    nrOfSentences++;
                    sentencesInConversation++; 
                }
        		teacherConversationLengthSum += sentencesInConversation;
                conversationLengthSum += sentencesInConversation;
        		nrOfTeacherConversations++;
        		nrOfConversations++;	
        	}
        	
        	else if (tt.getSpeaker().contains("S"))	{
        		int sentencesInConversation = 0;
        		for (Sentence sent : JCasUtil.selectCovered(jcas, Sentence.class, tt)) {
                    int wordsInSentence = 0;
                    
                    List<String> tokens = JCasUtil.toText(JCasUtil.selectCovered(Token.class, sent));
                    List<String> cleanedTokens = NGramUtils.removeSpecialCharacters(tokens);
                    
                    for (String t : cleanedTokens) {
                    	int tokenLength = t.length();
                    	if	(tokenLength > 0)	{
                    	    
                    	    nrOfStudentWords++;
                    	    nrOfWords++;
                    	    wordsInSentence++;
                    	    studentWordLengthSum += tokenLength;
                    	    wordLengthSum += tokenLength;
                    	}
    
                    }
                    studentSentenceLengthSum += wordsInSentence;
                    sentenceLengthSum += wordsInSentence;
                    nrOfStudentSentences++;
                    nrOfSentences++;
                    sentencesInConversation++;
                }
        		studentConversationLengthSum += sentencesInConversation;
                conversationLengthSum += sentencesInConversation;
        		nrOfStudentConversations++;
        		nrOfConversations++;
        	}
        	
        }
        
        double averageConversationLength = conversationLengthSum / nrOfConversations;
        double averageTeacherConversationLength = teacherConversationLengthSum / nrOfTeacherConversations;
        double averageStudentConversationLength = studentConversationLengthSum / nrOfStudentConversations;
        
        double averageSentenceLength = sentenceLengthSum / nrOfSentences;
        double averageTeacherSentenceLength = teacherSentenceLengthSum / nrOfTeacherSentences;
        double averageStudentSentenceLength = studentSentenceLengthSum / nrOfStudentSentences;
        
        double averageWordLength = wordLengthSum / nrOfWords;
        double averageTeacherWordLength = teacherWordLengthSum / nrOfTeacherWords;
        double averageStudentWordLength = studentWordLengthSum / nrOfStudentWords;
        
        List<Feature> featList = new ArrayList<Feature>();
        featList.add(new Feature(GROUP_PREFIX+ AV_CONVERSATION_LENGTH, averageConversationLength));
        featList.add(new Feature(GROUP_PREFIX+ AV_CONVERSATION_LENGTH + "_Teacher", averageTeacherConversationLength));
        featList.add(new Feature(GROUP_PREFIX+ AV_CONVERSATION_LENGTH + "_Student", averageStudentConversationLength));
        featList.add(new Feature(GROUP_PREFIX+ "TeacherStudentConversationLengthRatio", averageTeacherConversationLength/averageStudentConversationLength));
        
        featList.add(new Feature(GROUP_PREFIX+ AV_SENTENCE_LENGTH, averageSentenceLength));
        featList.add(new Feature(GROUP_PREFIX+ AV_SENTENCE_LENGTH + "_Teacher", averageTeacherSentenceLength));
        featList.add(new Feature(GROUP_PREFIX+ AV_SENTENCE_LENGTH + "_Student", averageStudentSentenceLength));
        featList.add(new Feature(GROUP_PREFIX+ "TeacherStudentSentenceLengthRatio", averageTeacherSentenceLength/averageStudentSentenceLength));
        
        featList.add(new Feature(GROUP_PREFIX+ AV_WORD_LENGTH, averageWordLength));
        featList.add(new Feature(GROUP_PREFIX+ AV_WORD_LENGTH + "_Teacher", averageTeacherWordLength));
        featList.add(new Feature(GROUP_PREFIX+ AV_WORD_LENGTH + "_Student", averageStudentWordLength));
        featList.add(new Feature(GROUP_PREFIX+ "TeacherStudentWordLengthRatio", averageTeacherWordLength/averageStudentWordLength));
        
        featList.add(new Feature(GROUP_PREFIX+ "TeacherStudentNrOfConversationsRatio", nrOfTeacherConversations/nrOfStudentConversations));
        featList.add(new Feature(GROUP_PREFIX+ "TeacherStudentNrOfSentencesRatio", nrOfTeacherSentences/nrOfStudentSentences));
        featList.add(new Feature(GROUP_PREFIX+ "TeacherStudentNrOfWordsRatio", nrOfTeacherWords/nrOfStudentWords));
        
        return featList;
    }

}