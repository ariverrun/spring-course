package ru.otus.hw.dao.reader;

import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.opencsv.bean.CsvToBeanBuilder;

public class CsvQuestionReaderImpl implements CsvQuestionReader {

    @Override
    public List<QuestionDto> readFromResourceFile(String fileName) throws QuestionReadException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (stream == null) {
            throw new QuestionReadException(String.format("Failed to read file `%s`", fileName));
        }

        try {
            return makeCsvToBeanBuilder(stream)
                                .build()
                                .parse();
            
        } catch (Throwable e) {
            throw new QuestionReadException(String.format("Failed to read file `%s`", fileName));
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
