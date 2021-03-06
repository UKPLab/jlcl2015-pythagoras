package pythagoras.featureExtractors.pos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import pythagoras.annotations.TranscribedText;
import pythagoras.featureExtractorUtils.S_AdjectivesMetaCollector;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.ADJ;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import de.tudarmstadt.ukp.dkpro.tc.api.features.meta.MetaCollector;
import de.tudarmstadt.ukp.dkpro.tc.api.features.meta.MetaDependent;
import de.tudarmstadt.ukp.dkpro.tc.exception.TextClassificationException;

public class S_AdjectivesFeatureExtractor
    extends FeatureExtractorResource_ImplBase
    implements MetaDependent, DocumentFeatureExtractor
{

    public static final String PARAM_S_ADJECTIVES_FD_FILE = "StudentAdjectivesFdFile";
    public static final String PARAM_USE_TOP_K_ADJECTIVES = "TopKAdjectives";

    @ConfigurationParameter(name = PARAM_S_ADJECTIVES_FD_FILE, mandatory = true)
    private String studentAdjectivesFdFile;

    @ConfigurationParameter(name = PARAM_USE_TOP_K_ADJECTIVES, mandatory = false)
    private int topKAdjectives = 500;
    
    public static final String PARAM_ADJ_SUBGROUPS_FILE_PATH = "adjectiveSubgroupsFilePath";
    @ConfigurationParameter(name = PARAM_ADJ_SUBGROUPS_FILE_PATH, mandatory = true)
    private String adjectiveSubgroupsFilePath;

    private Set<String> topAdjectives;
    
    public List<Feature> extract(JCas jcas)
        throws TextClassificationException
    {
        List<Feature> featList = new ArrayList<Feature>();
        List<String> features = new ArrayList<String>();
        List<String> cleanedAdjectives = new ArrayList<String>();
        
        if (adjectiveSubgroupsFilePath == null || adjectiveSubgroupsFilePath.isEmpty()) {
            System.out.println("Path to word list must be set!");
        }
        List<String> subgroups = null;
        
        for	(TranscribedText tt : JCasUtil.select(jcas, TranscribedText.class))	{
    		if	(tt.getSpeaker().contains("T"))	{
    			List<Token>	tokens =  JCasUtil.selectCovered(jcas, Token.class, tt);
    			
    			for	(Token t : tokens)	{
    				String pos = t.getPos().getPosValue();
    				if	(pos.startsWith("ADJ"))	{
    					String adjectiveLemma = t.getLemma().getValue().toLowerCase();
    					features.add(adjectiveLemma);
    				}
    			}
    		}
        }

        try {
            subgroups = FileUtils.readLines(new File(adjectiveSubgroupsFilePath));
            for (String group : subgroups) {
                featList.addAll(countAdverbHits(group, features));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }        

        for (String topAdj : topAdjectives) {
            if (features.contains(topAdj)) {
                featList.addAll(Arrays.asList(new Feature("S_feature_adj_Bool_" + topAdj, 1)));
            }
            else {
                featList.addAll(Arrays.asList(new Feature("S_feature_adj_Bool_" + topAdj, 0)));
            }
        }
        return featList;
    }

    private List<Feature> countAdverbHits(String wordListName, List<String> adj)
            throws TextClassificationException
        {
        	
        	String wordListPath = "src/main/resources/adjectiveSubgroups/" + wordListName;
            List<String> adjectives = null;
            try {
            	adjectives = FileUtils.readLines(new File(wordListPath), "utf-8");
            }
            catch (IOException e) {
                throw new TextClassificationException(e);
            }
            double adjectiveCount = 0;
            for (String adjective : adj) {
                if (adjectives.contains(adjective)) {
                	adjectiveCount++;
                }
            }
            double numAdjectives = adj.size();
            // name the feature same as wordlist
            return Arrays.asList(new Feature("S_feature_adj_ratio_" + wordListName,
            		numAdjectives > 0 ? (adjectiveCount / numAdjectives) : 0));
        }
    
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }
        topAdjectives = getTopAdjectives();

        return true;
    }

    protected Set<String> getTopAdjectives()
        throws ResourceInitializationException
    {
        try {
            FrequencyDistribution<String> trainingFD = new FrequencyDistribution<String>();
            trainingFD.load(new File(studentAdjectivesFdFile));
            return new HashSet<String>(trainingFD.getMostFrequentSamples(topKAdjectives));
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        catch (ClassNotFoundException e) {
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public List<Class<? extends MetaCollector>> getMetaCollectorClasses()
    {
        List<Class<? extends MetaCollector>> metaCollectorClasses = new ArrayList<Class<? extends MetaCollector>>();
        metaCollectorClasses.add(S_AdjectivesMetaCollector.class);

        return metaCollectorClasses;
    }
    
  //Cleans up words of punctuation, special symbols
    public static List<String> removeSpecialCharacters(List<String> adjectiveList)	{
    	List<String> newAdjectiveList = new ArrayList<String>();
    	//System.out.println("Verb: " + verb + "\n");
    	for	(String adjective : adjectiveList)	{
    		String temp1 = adjective.replaceAll("[^\\p{L}\\p{Z}]","");				//Remove all punctuation and special symbols			
    		adjective = temp1;
    		
    		if	(!adjective.equals(""))
    			newAdjectiveList.add(adjective);
    	}
    	return(newAdjectiveList);
    }

}
