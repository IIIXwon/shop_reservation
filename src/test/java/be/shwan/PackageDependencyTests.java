package be.shwan;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = ReservationApplication.class)
public class PackageDependencyTests {
    private final String STUDY = "..modules.study..";
    private final String EVENT = "..modules.event..";

    private final String ACCOUNT = "..modules.account..";
    private final String TAG = "..modules.tag..";
    private final String ZONE = "..modules.zone..";

    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("be.shwan.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("be.shwan.modules..");
    @ArchTest
    ArchRule studyPackageRules = classes().that().resideInAPackage(STUDY).should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(STUDY, EVENT);

    @ArchTest
    ArchRule eventPackageRules = classes().that().resideInAPackage(EVENT).should().accessClassesThat()
            .resideInAnyPackage(STUDY, EVENT, ACCOUNT);

    @ArchTest
    ArchRule accountPackageRules = classes().that().resideInAPackage(ACCOUNT).should().accessClassesThat()
            .resideInAnyPackage(ACCOUNT, ZONE, TAG);

    @ArchTest
    ArchRule cycleCheck = slices().matching("be.shwan.modules.(*)..")
            .should().beFreeOfCycles();
}
