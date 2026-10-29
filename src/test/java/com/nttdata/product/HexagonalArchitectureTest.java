package com.nttdata.product;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Reglas que garantizan que el proyecto respete la arquitectura hexagonal. Si alguna falla, el build falla.
 */
@AnalyzeClasses(packages = "com.nttdata.product", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

	@ArchTest
	static final ArchRule arquitecturaHexagonal = onionArchitecture()
			.domainModels("..domain.model..", "..domain.exception..")
			.domainServices("..domain.port..")
			.applicationServices("..application..")
			.adapter("rest", "..infrastructure.adapter.in.rest..")
			.adapter("persistence", "..infrastructure.adapter.out.persistence..")
			.adapter("config", "..infrastructure.config..");

	@ArchTest
	static final ArchRule dominioNoDependeDeFrameworks = noClasses().that().resideInAPackage("..domain..")
			.should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta..", "javax..");

	@ArchTest
	static final ArchRule aplicacionNoDependeDeSpring = noClasses().that().resideInAPackage("..application..")
			.should().dependOnClassesThat().resideInAnyPackage("org.springframework..");

	@ArchTest
	static final ArchRule puertosSonInterfacesOComandos = classes().that().resideInAPackage("..domain.port..")
			.and().areTopLevelClasses()
			.and().haveSimpleNameNotEndingWith("Command")
			.should().beInterfaces();

	@ArchTest
	static final ArchRule casosDeUsoTerminanEnUseCase = classes().that().resideInAPackage("..domain.port.in..")
			.and().areInterfaces()
			.should().haveSimpleNameEndingWith("UseCase");

	@ArchTest
	static final ArchRule puertosDeSalidaTerminanEnPort = classes().that().resideInAPackage("..domain.port.out..")
			.should().haveSimpleNameEndingWith("Port");

}
