package ru.otus.hw.dao.reader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;

import com.opencsv.bean.CsvToBeanBuilder;

import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.exceptions.QuestionReadException;

@Service
public class CsvQuestionReaderImpl implements CsvQuestionReader {

    @Override
    public List<QuestionDto> readFromResourceFile(String fileName) throws QuestionReadException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            return makeCsvToBeanBuilder(stream)
                                .build()
                                .parse();
            
        } catch (Exception e) {
            throw new QuestionReadException(String.format("Failed to read file `%s`", fileName), e);
        }        
    }


    private CsvToBeanBuilder<QuestionDto> makeCsvToBeanBuilder(InputStream stream) {
        return new CsvToBeanBuilder<QuestionDto>(new InputStreamReader(stream, StandardCharsets.UTF_8))
                            .withSkipLines(1)
                            .withSeparator(';')
                            .withIgnoreLeadingWhiteSpace(true)                                            
                            .withType(QuestionDto.class);        
    }    
}
