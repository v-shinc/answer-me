import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.SerializationUtils;
import org.springframework.core.io.ClassPathResource;

public class Graph2vec {
	public static int windowSize = 3;
	public final static String FILE_PATH = "D:\\Study\\kb\\xaa_1";
	public final static String VEC_PATH = "vec2.ser";
	public final static String CACHE_SER = "cache.ser";

	private Word2Vec vec;
	private SentenceIterator iter;
	private TokenizerFactory tokenizer;

	public Graph2vec(String path) throws Exception {
		this.iter = new LineSentenceIterator(new File(path));
		tokenizer = new DefaultTokenizerFactory();
	}

	public static void main(String[] args) throws Exception {
		if (args.length >= 1)
			new Graph2vec(args[0]).train();
		else {
			ClassPathResource resource = new ClassPathResource("xaa_1");
			File f = resource.getFile();
			new Graph2vec(f.getAbsolutePath()).train();
		}
	}

	public void train() throws Exception {
		VocabCache cache;
		if (vec == null && !new File(VEC_PATH).exists()) {
			cache = new InMemoryLookupCache.Builder().lr(2e-5)
					.vectorLength(100).build();

			vec = new Word2Vec.Builder().minWordFrequency(3).vocabCache(cache)
					.windowSize(5).layerSize(100).iterate(iter)
					.tokenizerFactory(tokenizer).build();
			vec.setCache(cache);
			vec.fit();

			SerializationUtils.saveObject(vec, new File(VEC_PATH));
			SerializationUtils.saveObject(cache, new File(CACHE_SER));

		}

		else {
			vec = SerializationUtils.readObject(new File(VEC_PATH));
			cache = SerializationUtils.readObject(new File(CACHE_SER));
			vec.setCache(cache);

			for (String s : cache.words()) {
				System.out.println(s);
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			String line;
			System.out.println("Print similarity");
			while ((line = reader.readLine()) != null) {

				String[] split = line.split(",");
				if (cache.indexOf(split[0]) < 0) {
					System.err.println("Word " + split[0] + " not in vocab");
					continue;
				}
				if (cache.indexOf(split[01]) < 0) {
					System.err.println("Word " + split[1] + " not in vocab");
					continue;
				}
				System.out.println(vec.similarity(split[0], split[1]));
			}

		}
	}
}
