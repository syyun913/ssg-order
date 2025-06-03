package com.ssg.order.convention;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.ssg.order.domain.common.annotation.UseCase;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@DisplayName("모듈 컨벤션 규칙 테스트")
public class ModuleConventionRulesTest extends ArchUnitSupport {
    @Nested
    @DisplayName("패키지 의존성을 검증한다.")
    class CheckPackageDependency {

        @Test
        @DisplayName("order 하위 패키지 간에 순환 의존성이 없어야 한다.")
        void checkCyclicDependency() {
            ArchRule rule = slices().matching("..order.(*)..")
                .should().beFreeOfCycles();

            rule.check(IMPORTED_CLASSES);
        }

        @Test
        @DisplayName("order.domain 하위 패키지 간에 순환 의존성이 없어야 한다.")
        void checkDomainCyclicDependency() {
            ArchRule rule = slices().matching("..order.domain.(**)..")
                .should().beFreeOfCycles();

            rule.check(IMPORTED_CLASSES);
        }

        // domain -> api, infra: 의존 X
        @Test
        @DisplayName("domain 패키지는 api, infra 패키지에 의존하지 않아야 한다.")
        void checkDomainPackageDependency() {
            ArchRule rule = noClasses().that().resideInAPackage("..order.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                    "..order.api..",
                    "..order.infra.."
                );

            rule.check(IMPORTED_CLASSES);
        }

        // api -> infra: 의존 X
        @Test
        @DisplayName("api 패키지는 infra 패키지에 의존하지 않아야 한다.")
        void checkApiPackageDependency() {
            ArchRule rule = noClasses().that().resideInAPackage("..order.api..")
                .should().dependOnClassesThat()
                .resideInAPackage("..order.infra..");

            rule.check(IMPORTED_CLASSES);
        }

        // infra -> api: 의존 X
        @Test
        @DisplayName("infra 패키지는 api 패키지에 의존하지 않아야 한다.")
        void checkinfraPackageDependency() {
            ArchRule rule = noClasses().that()
                .resideInAPackage("..order.infra..")
                .should().dependOnClassesThat()
                .resideInAPackage("..order.api..");

            rule.check(IMPORTED_CLASSES);
        }
    }

    @Test
    @DisplayName("아키텍처 레이어 규칙을 검증한다.")
    void checkLayeredArchitectureRule() {
        ArchRule rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer("api").definedBy("..order.api..")
            .layer("domain").definedBy("..order.domain..")
            .layer("infra").definedBy("..order.infra..")

            .whereLayer("api").mayNotBeAccessedByAnyLayer()
            .whereLayer("domain").mayOnlyBeAccessedByLayers(
                "api",
                "infra"
            )
            .whereLayer("infra").mayOnlyBeAccessedByLayers("domain")

            .because("api 모듈은 다른 모듈에서 접근하지 않아야 한다.")
            .because("domain 모듈은 api, infra 모듈에서만 접근할 수 있다.")
            .because("infra 모듈은 domain 모듈에서만 접근할 수 있다.");

        rule.check(IMPORTED_CLASSES);
    }

    @Nested
    @DisplayName("컨벤션 규칙에 맞는지 검증한다.")
    class ConventionRulesTest {

        @Test
        @DisplayName("Entity 컨벤션 검증")
        void checkPersistenceEntityRules() {
            ArchRule rule = classes().that()
                .areAnnotatedWith(Entity.class)
                .and().haveNameNotMatching(".*\\$.*")  // 내부 클래스를 제외
                .should().haveSimpleNameEndingWith("Entity")
                .andShould().beAnnotatedWith(Table.class)
                .because("@Entity가 붙은 클래스명은 Entity로 끝나야 한다.")
                .because("@Entity가 붙은 클래스는 @Table을 사용해야 한다.");

            rule.check(IMPORTED_CLASSES);
        }

        @Test
        @DisplayName("Repository 컨벤션 검증")
        void checkPersistenceRepositoryRules() {
            ArchRule rule = classes().that()
                .areAnnotatedWith(Repository.class)
                .should().haveSimpleNameEndingWith("Repository")
                .andShould().notBeAnnotatedWith(Service.class)
                .andShould().notBeAnnotatedWith(Component.class)
                .andShould().notBeAnnotatedWith(Transactional.class)
                .because("@Repository가 붙은 클래스명은 Repository로 끝나야 한다.")
                .because("@Repository가 붙은 클래스는 @Service를 사용하지 않아야 한다.")
                .because("@Repository가 붙은 클래스는 @Component 사용하지 않아야 한다.")
                .because("@Repository가 붙은 클래스는 @Transactional을 사용하지 않아야 한다.");

            rule.check(IMPORTED_CLASSES);
        }

        @Test
        @DisplayName("UseCase Impl 규칙을 검증한다.")
        void checkUseCaseImplRules() {
            ArchRule rule = classes().that()
                .resideInAPackage("..usecase")
                .and().areNotInterfaces()
                .and().areNotAnonymousClasses()
                .should().haveSimpleNameEndingWith("UseCaseImpl")
                .andShould().beAnnotatedWith(UseCase.class)
                .andShould().notBeAnnotatedWith(Service.class)
                .andShould().notBeAnnotatedWith(Component.class)
                .andShould().notBeAnnotatedWith(Transactional.class)
                .andShould().bePackagePrivate()
                .because("usecase 패키지 내에 (인터페이스가 아닌) 클래스의 이름은 UseCaseImpl로 끝나야 한다.")
                .because("usecase 패키지 내에 (인터페이스가 아닌) 클래스는 @UseCase를 사용해야 한다.")
                .because("usecase 패키지 내에 (인터페이스가 아닌) 클래스는 @Service를 사용하지 않아야 한다.")
                .because("usecase 패키지 내에 (인터페이스가 아닌) 클래스는 @Component를 사용하지 않아야 한다.")
                .because("usecase 패키지 내에 (인터페이스가 아닌) 클래스는 @Transactional을 사용하지 않아야 한다.")
                .because("usecase 패키지 내에 (인터페이스가 아닌) 클래스는 package-private여야 한다.");

            rule.check(IMPORTED_CLASSES);
        }

        @Test
        @DisplayName("상수는 대문자로 작성해야 한다.")
        void checkConstantRules() {
            ArchRule rule = fields().that()
                .areDeclaredInClassesThat()
                .haveSimpleNameNotStartingWith("Q").and() // Q로 시작하는 클래스 제외
                .arePublic().and()
                .areStatic().and()
                .areFinal()
                .should().haveNameMatching("^[A-Z0-9_]*$")
                .because("public static final 예약어가 붙은 필드는 대문자여야 한다.");

            rule.check(IMPORTED_CLASSES);
        }
    }
}
