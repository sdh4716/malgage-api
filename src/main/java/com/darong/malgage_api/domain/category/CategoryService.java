package com.darong.malgage_api.domain.category;

import com.darong.malgage_api.domain.category.dto.CategoryResponseDto;
import com.darong.malgage_api.domain.category.repository.CategoryDefaultRepository;
import com.darong.malgage_api.domain.category.repository.UserCategoryRepository;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryDefaultRepository categoryDefaultRepository;
    private final UserCategoryRepository userCategoryRepository;

    public List<CategoryResponseDto> getCategoriesForUser(User user) {
        List<CategoryResponseDto> defaults = categoryDefaultRepository.findAll()
                .stream()
                .map(CategoryResponseDto::from)
                .toList();

        List<CategoryResponseDto> customs = userCategoryRepository.findByUserAndEnabledTrue(user)
                .stream()
                .map(CategoryResponseDto::from)
                .toList();

        return Stream.concat(defaults.stream(), customs.stream())
                .sorted(Comparator.comparing(CategoryResponseDto::getName))
                .toList();
    }
}
