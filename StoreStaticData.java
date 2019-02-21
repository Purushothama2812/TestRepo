package com.thinkiit.assay.AssayServer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.common.io.ByteStreams;
import com.thinkiit.assay.AssayServer.db.models.questions.Answer;
import com.thinkiit.assay.AssayServer.db.models.questions.Comprehension;
import com.thinkiit.assay.AssayServer.db.models.questions.Question;
import com.thinkiit.assay.AssayServer.db.models.questions.QuestionBank;
import com.thinkiit.assay.AssayServer.db.models.student.ContactDetails;
import com.thinkiit.assay.AssayServer.db.models.student.StudentProfile;
import com.thinkiit.assay.AssayServer.db.models.test.TestInfo;
import com.thinkiit.assay.AssayServer.db.models.test.TestSection;
import com.thinkiit.assay.AssayServer.db.models.test.TestSectionQuestionMapping;
import com.thinkiit.assay.AssayServer.db.models.test.TestTemplate;
import com.thinkiit.assay.AssayServer.db.models.util.AdminDetails;
import com.thinkiit.assay.AssayServer.db.models.util.Batch;
import com.thinkiit.assay.AssayServer.db.models.util.Chapters;
import com.thinkiit.assay.AssayServer.db.models.util.ClassType;
import com.thinkiit.assay.AssayServer.db.models.util.DifficultyLevel;
import com.thinkiit.assay.AssayServer.db.models.util.ExamType;
import com.thinkiit.assay.AssayServer.db.models.util.QuestionType;
import com.thinkiit.assay.AssayServer.db.models.util.SubChapters;
import com.thinkiit.assay.AssayServer.db.models.util.Subject;
import com.thinkiit.assay.AssayServer.domain.Constants;
import com.thinkiit.assay.AssayServer.repository.util.AdminDetailRepository;
import com.thinkiit.assay.AssayServer.repository.util.BatchRepository;
import com.thinkiit.assay.AssayServer.repository.util.ChapterRepository;
import com.thinkiit.assay.AssayServer.repository.util.ClassTypeRepository;
import com.thinkiit.assay.AssayServer.repository.util.DifficultyLevelRepository;
import com.thinkiit.assay.AssayServer.repository.util.ExamTypeRepository;
import com.thinkiit.assay.AssayServer.repository.util.QuestionTypeRepository;
import com.thinkiit.assay.AssayServer.repository.util.SubChapterRepository;
import com.thinkiit.assay.AssayServer.repository.util.SubjectRepository;
import com.thinkiit.assay.AssayServer.service.util.QuestionBankService;
import com.thinkiit.assay.AssayServer.service.util.StudentServiceUtil;
import com.thinkiit.assay.AssayServer.service.util.TestTemplateServiceUtil;
import com.thinkiit.assay.AssayServer.service.util.ValidateDataUtil;

@Service
public class StoreStaticData {

	@Autowired
	private QuestionTypeRepository questionTypeRepository;

	@Autowired
	private DifficultyLevelRepository difficultyLevelRepository;

	@Autowired
	private ClassTypeRepository classTypeRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private ExamTypeRepository examTypeRepository;

	@Autowired
	private ChapterRepository chapterRepository;

	@Autowired
	private SubChapterRepository subChapterRepository;

	@Autowired
	private BatchRepository batchRepository;

	@Autowired
	private QuestionBankService questionBankService;

	@Autowired
	private ValidateDataUtil validateDataUtil;

	@Autowired
	private TestTemplateServiceUtil testTemplateServiceUtil;
	
	@Autowired
	private AdminDetailRepository adminDetailRepository;
	
	@Autowired
	private StudentServiceUtil studentServiceUtil;

/**

added comment */
	public String storeStaticData() {
		this.storeQuestionType();
		this.storeDifficultyLevel();
		this.storeStaticClassType();
		this.storStaticExamType();
		this.subjectStaticType();
		this.storeStaticBatches();
		this.createAdminUser();
		return "Saved Success";
	}

	private void createAdminUser() {
		AdminDetails adminDetails =  new AdminDetails();
		adminDetails.setUserName("admin@gmail.com");
		adminDetails.setAdminName("Admin");
		adminDetails.setPassword("admin");
		List<AdminDetails> findAll = adminDetailRepository.findAll();
		if(findAll!=null && findAll.isEmpty()){
			adminDetailRepository.save(adminDetails);
		}
	}

	public void storeQuestionType() {
		List<QuestionType> questionTypeList = new ArrayList<QuestionType>();
		QuestionType questionType1 = new QuestionType();
		questionType1.setQuestionType(Constants.INTEGER_TYPE_QUESTION);
		questionType1.setQuestionTypeDesc(
				"The answer to each question is a SINGLE DIGIT INTEGER ranging from 0 to 9, both inclusive. For each question, enter the answer in the given text box.");
		questionType1.setQuestionSearchText(Constants.ITQ);
		questionTypeList.add(questionType1);

		QuestionType questionType2 = new QuestionType();
		questionType2.setQuestionType(Constants.SINGLE_TYPE_QUESTION);
		questionType2.setQuestionTypeDesc(
				"The answer to each question is ONE of the options provided. For each question, select corresponding answer options by clicking the radio buttons");
		questionType2.setQuestionSearchText(Constants.SCQ);
		questionTypeList.add(questionType2);

		QuestionType questionType3 = new QuestionType();
		questionType3.setQuestionType(Constants.MULTIPLE_TYPE_QUESTION);
		questionType3.setQuestionTypeDesc(
				"The answer to each question is ONE OR MORE THAN ONE of the options provided. For each question, select all the corresponding answer options by clicking the check boxes");
		questionType3.setQuestionSearchText(Constants.MCQ);
		questionTypeList.add(questionType3);

		QuestionType questionType5 = new QuestionType();
		questionType5.setQuestionType(Constants.ASSERTION_TYPE_QUESTION);
		questionType5.setQuestionTypeDesc("There will be two statements and options will be based on the statements");
		questionType5.setQuestionSearchText(Constants.ARQ);
		questionTypeList.add(questionType5);

		QuestionType questionType6 = new QuestionType();
		questionType6.setQuestionType(Constants.MATRIX_TYPE_QUESTION);
		questionType6.setQuestionTypeDesc(
				"There are two columns. Match the entries in the first column with the ones in column two.One or more entries in column one may match with one or more entries in column two.");
		questionType6.setQuestionSearchText(Constants.MMQ);

		QuestionType questionType4 = new QuestionType();
		questionType4.setQuestionType(Constants.COMPREHENSION_TYPE_QUESTION);
		questionType4.setQuestionTypeDesc(
				"There is a paragraph given based on which there are questions to be answered. Read the paragraph carefully and answer the questions that follow.");
		questionType4.setQuestionSearchText(Constants.CMP);
		questionTypeList.add(questionType4);
		questionTypeList.add(questionType6);

		List<QuestionType> findAll = questionTypeRepository.findAll();
		if (findAll.isEmpty()) {
			questionTypeRepository.saveAll(questionTypeList);
		}
	}

	public void storeDifficultyLevel() {
		List<DifficultyLevel> difficultyLevelList = new ArrayList<DifficultyLevel>();
		DifficultyLevel difficultyLevel1 = new DifficultyLevel();
		difficultyLevel1.setDifficultyLevelText("Level-1");
		difficultyLevelList.add(difficultyLevel1);

		DifficultyLevel difficultyLevel2 = new DifficultyLevel();
		difficultyLevel2.setDifficultyLevelText("Level-2");
		difficultyLevelList.add(difficultyLevel2);

		DifficultyLevel difficultyLevel3 = new DifficultyLevel();
		difficultyLevel3.setDifficultyLevelText("Level-3");
		difficultyLevelList.add(difficultyLevel3);

		DifficultyLevel difficultyLevel4 = new DifficultyLevel();
		difficultyLevel4.setDifficultyLevelText("Level-4");
		difficultyLevelList.add(difficultyLevel4);

		List<DifficultyLevel> findAll = difficultyLevelRepository.findAll();
		if (findAll.isEmpty()) {
			difficultyLevelRepository.saveAll(difficultyLevelList);
		}
	}

	public void storeStaticClassType() {
		List<ClassType> classTypeList = new ArrayList<ClassType>();
		ClassType classType1 = new ClassType();
		classType1.setClassTypeName("11th");
		classTypeList.add(classType1);

		ClassType classType2 = new ClassType();
		classType2.setClassTypeName("12th");
		classTypeList.add(classType2);

		List<ClassType> findAll = classTypeRepository.findAll();
		if (findAll.isEmpty()) {
			classTypeRepository.saveAll(classTypeList);
		}
	}

	public void storStaticExamType() {
		List<ExamType> examTypeList = new ArrayList<ExamType>();
		ExamType examType1 = new ExamType();
		examType1.setExamName("JEE Advanced-Full");
		examType1.setExamType(Constants.FULL);
		examType1.setTestType(Constants.testTypeADV);

		ExamType examType2 = new ExamType();
		examType2.setExamName("JEE Advanced-Part");
		examType2.setExamType(Constants.PART);
		examType2.setTestType(Constants.testTypeADV);

		ExamType examType3 = new ExamType();
		examType3.setExamName("JEE Main-Full");
		examType3.setExamType(Constants.FULL);
		examType3.setTestType(Constants.testTypeMAIN);

		ExamType examType4 = new ExamType();
		examType4.setExamName("JEE Main-Part");
		examType4.setExamType(Constants.PART);
		examType4.setTestType(Constants.testTypeMAIN);

		ExamType examType5 = new ExamType();
		examType5.setExamName("Chapterwise");
		examType5.setExamType(Constants.CHAPTERWISE);
		examType5.setTestType(Constants.testTypeMAIN);

		ExamType examType6 = new ExamType();
		examType6.setExamName(Constants.testTypeNEET);
		examType6.setExamType(Constants.FULL);
		examType6.setTestType(Constants.testTypeADV);

		ExamType examType7 = new ExamType();
		examType7.setExamName(Constants.testTypeGeneric);
		examType7.setExamType(Constants.FULL);
		examType7.setTestType(Constants.testTypeADV);

		examTypeList.add(examType1);
		examTypeList.add(examType2);
		examTypeList.add(examType3);
		examTypeList.add(examType4);
		examTypeList.add(examType5);

		List<ExamType> findAll = examTypeRepository.findAll();
		if (findAll.isEmpty()) {
			examTypeRepository.saveAll(examTypeList);
		}
		ExamType examTypeDB = examTypeRepository.findByExamName(Constants.testTypeNEET);
		if (examTypeDB == null) {
			examTypeRepository.save(examType6);
			examTypeList.add(examType6);
		} else {
			examTypeList.add(examTypeDB);
		}

		ExamType examTypeDB1 = examTypeRepository.findByExamName(Constants.testTypeGeneric);
		if (examTypeDB1 == null) {
			examTypeRepository.save(examType7);
			examTypeList.add(examType7);
		} else {
			examTypeList.add(examTypeDB1);
		}

	}

	public void subjectStaticType() {

		List<ExamType> findAll = examTypeRepository.findAll();
		List<ExamType> jeExamList = new ArrayList<ExamType>();
		List<ExamType> neetExamList = new ArrayList<ExamType>();
		List<ExamType> genericExamList = new ArrayList<ExamType>();

		for (ExamType examType : findAll) {
			if (examType.getExamName().startsWith("JEE") || examType.getExamName().startsWith("Cha")) {
				jeExamList.add(examType);
			} else if (examType.getExamName().startsWith("Gen")) {
				genericExamList.add(examType);
			} else {
				neetExamList.add(examType);
			}
		}

		List<Subject> subjectList = new ArrayList<Subject>();
		Subject subject = new Subject();
		// subject.setSubjectId(1l);
		subject.setSubjectName(Constants.PHYSICS);
		// subject.getExamTypeList().addAll(findAll);

		Subject subject1 = new Subject();
		// subject1.setSubjectId(2l);
		subject1.setSubjectName(Constants.CHEMISTRY);
		// subject1.getExamTypeList().addAll(findAll);

		Subject subject2 = new Subject();
		// subject2.setSubjectId(3l);
		subject2.setSubjectName(Constants.MATHS);
		// subject2.getExamTypeList().addAll(jeExamList);
		// subject2.getExamTypeList().addAll(genericExamList);

		Subject subject3 = new Subject();
		// subject3.setSubjectId(4l);
		subject3.setSubjectName(Constants.BIOLOGY);
		// subject3.getExamTypeList().addAll(neetExamList);
		// subject3.getExamTypeList().addAll(genericExamList);

		Subject findBySubjectPName = subjectRepository.findBySubjectName(Constants.PHYSICS);
		if (findBySubjectPName == null) {
			subjectList.add(subject);
		} else {
			subject.setSubjectId(findBySubjectPName.getSubjectId());
			subjectList.add(subject);
		}
		Subject findBySubjectCName = subjectRepository.findBySubjectName(Constants.CHEMISTRY);
		if (findBySubjectCName == null) {
			subjectList.add(subject1);
		} else {
			subject1.setSubjectId(findBySubjectCName.getSubjectId());
			subjectList.add(subject1);
		}
		Subject findBySubjectMName = subjectRepository.findBySubjectName(Constants.MATHS);
		if (findBySubjectMName == null) {
			subjectList.add(subject2);
		} else {
			subject2.setSubjectId(findBySubjectMName.getSubjectId());
			subjectList.add(subject2);
		}
		Subject findBySubjectBName = subjectRepository.findBySubjectName(Constants.BIOLOGY);
		if (findBySubjectBName == null) {
			subjectList.add(subject3);
		} else {
			subject3.setSubjectId(findBySubjectBName.getSubjectId());
			subjectList.add(subject3);
		}
		subjectRepository.saveAll(subjectList);

	}

	public List<Chapters> addChapters() {
		List<Chapters> chapterListSaved = new ArrayList<Chapters>();
		List<ClassType> classTypeList = classTypeRepository.findAll();
		if (classTypeList != null) {
			ClassType classType = classTypeList.get(0);
			List<Subject> subjectList = subjectRepository.findAll();
			for (Subject subject : subjectList) {
				List<Chapters> chapterList = chapterRepository.findBySubjectAndClassType(subject, classType);
				int size = 1;
				if (chapterList != null)
					size = chapterList.size() + 1;
				Chapters chapter = new Chapters();
				chapter.setChapterName("SampleChapter-" + subject.getSubjectName() + "-" + size);
				chapter.setClassType(classType);
				chapter.setSubject(subject);
				chapterRepository.save(chapter);
				SubChapters subChapter1 = new SubChapters();
				subChapter1.setSubChapterName("Sub Chapter 1 ");
				subChapter1.setChapter(chapter);
				subChapterRepository.save(subChapter1);
				SubChapters subChapter2 = new SubChapters();
				subChapter2.setSubChapterName("Sub Chapter 2 ");
				subChapter2.setChapter(chapter);
				subChapterRepository.save(subChapter2);
				chapterListSaved.add(chapter);
			}
		}
		return chapterListSaved;
	}

	public void storeStaticBatches() {
		List<Batch> batchList = batchRepository.findAll();
		if (batchList == null || batchList.isEmpty()) {
			List<ClassType> classTypeList = classTypeRepository.findAll();
			Batch batch1 = new Batch();
			batch1.setBatchName("Batch1");
			batch1.setBatchYear("2018");
			batch1.setClassType(new HashSet<ClassType>(classTypeList));
			batchRepository.save(batch1);

			Batch batch2 = new Batch();
			batch2.setBatchName("Batch2");
			batch2.setBatchYear("2018");
			batch2.setClassType(new HashSet<ClassType>(classTypeList));
			batchRepository.save(batch2);
		}
	}

	public List<QuestionBank> addQuestions() throws Exception {
		DifficultyLevel difficultyLevel = difficultyLevelRepository.findAll().get(0);
		List<QuestionType> questionTypeList = questionTypeRepository.findAll();
		List<QuestionBank> questionBankList = new ArrayList<QuestionBank>();
		List<SubChapters> subchapters = subChapterRepository.findAll();
		byte[] arqContent = ByteStreams
				.toByteArray(new ClassPathResource("SampleHTMLFiles\\Q1-ARQ_pdf_question.html").getInputStream());
		byte[] mmqContent = ByteStreams.toByteArray(
				new ClassPathResource("SampleHTMLFiles\\Q1-MMQ_pdf_question_pdf_compress.html").getInputStream());
		byte[] scqContent = ByteStreams
				.toByteArray(new ClassPathResource("SampleHTMLFiles\\Q2-SCQ_pdf_question.html").getInputStream());
		for (SubChapters subChapter : subchapters) {
			QuestionBank questionSCQ = getQuestionBankObject(questionTypeList.get(1), scqContent, difficultyLevel,
					subChapter, true);
			questionSCQ.getQuestionData().setQuestionData(scqContent);
			questionSCQ.getQuestionData().setNoOfOptions(4);
			questionSCQ.getQuestionData().setQuestionOptionText("A&B&C&D");
			questionSCQ.getAnswerData().setAnswerInput("B");
			QuestionBank questionARQ = getQuestionBankObject(questionTypeList.get(3), arqContent, difficultyLevel,
					subChapter, true);
			questionARQ.getQuestionData().setNoOfOptions(4);
			questionARQ.getQuestionData().setQuestionOptionText("A&B&C&D");
			questionARQ.getAnswerData().setAnswerInput("B");
			questionARQ.getQuestionData().setQuestionData(arqContent);
			QuestionBank questionMMQ = getQuestionBankObject(questionTypeList.get(5), mmqContent, difficultyLevel,
					subChapter, true);
			questionMMQ.getQuestionData().setQuestionData(mmqContent);
			questionMMQ.getQuestionData().setNoOfOptions(4);
			questionMMQ.getQuestionData().setQuestionOptionText("A&B&C&D-P&Q&R&S");
			questionMMQ.getAnswerData().setAnswerInput("AP&BP&AQ");
			questionBankList.add(questionBankService.saveQuestion(questionSCQ));
			questionBankList.add(questionBankService.saveQuestion(questionARQ));
			questionBankList.add(questionBankService.saveQuestion(questionMMQ));
		}
		for (QuestionBank questionMMQ : questionBankList) {
			validateDataUtil.setNullAttributeToQuestionSubChapter(questionMMQ);
		}
		return questionBankList;
	}

	public List<QuestionBank> addQuestionsNonWord() throws Exception {
		DifficultyLevel difficultyLevel = difficultyLevelRepository.findAll().get(0);
		List<QuestionType> questionTypeList = questionTypeRepository.findAll();
		List<QuestionBank> questionBankList = new ArrayList<QuestionBank>();
		List<SubChapters> subchapters = subChapterRepository.findAll();
		byte[] arqContent = ByteStreams
				.toByteArray(new ClassPathResource("SampleHTMLFiles\\Q1-ARQ_pdf_questionW2.html").getInputStream());
		byte[] mmqContent = ByteStreams.toByteArray(
				new ClassPathResource("SampleHTMLFiles\\Q1-MMQ_pdf_question_pdf_compressW3.html").getInputStream());
		byte[] scqContent = ByteStreams
				.toByteArray(new ClassPathResource("SampleHTMLFiles\\Q2-SCQ_pdf_questionW1.html").getInputStream());
		for (SubChapters subChapter : subchapters) {
			QuestionBank questionSCQ = getQuestionBankObject(questionTypeList.get(1), scqContent, difficultyLevel,
					subChapter, false);
			questionSCQ.getQuestionData().setNoOfOptions(4);
			questionSCQ.getQuestionData().setQuestionOptionText("A&B&C&D");
			questionSCQ.getAnswerData().setAnswerInput("B");
			questionSCQ.getQuestionData().setQuestionData(scqContent);
			QuestionBank questionARQ = getQuestionBankObject(questionTypeList.get(3), arqContent, difficultyLevel,
					subChapter, false);
			questionARQ.getQuestionData().setQuestionData(arqContent);
			questionARQ.getQuestionData().setNoOfOptions(4);
			questionARQ.getQuestionData().setQuestionOptionText("A&B&C&D");
			questionARQ.getAnswerData().setAnswerInput("B");
			QuestionBank questionMMQ = getQuestionBankObject(questionTypeList.get(5), mmqContent, difficultyLevel,
					subChapter, false);
			questionMMQ.getQuestionData().setQuestionData(arqContent);
			questionMMQ.getQuestionData().setNoOfOptions(4);
			questionMMQ.getQuestionData().setQuestionOptionText("A&B&C&D-P&Q&R&S");
			questionMMQ.getAnswerData().setAnswerInput("AP&BP&AQ");
			questionMMQ.getQuestionData().setQuestionData(mmqContent);
			questionBankList.add(questionBankService.saveQuestion(questionSCQ));
			questionBankList.add(questionBankService.saveQuestion(questionARQ));
			questionBankList.add(questionBankService.saveQuestion(questionMMQ));
		}
		for (QuestionBank questionMMQ : questionBankList) {
			validateDataUtil.setNullAttributeToQuestionSubChapter(questionMMQ);
		}
		return questionBankList;
	}

	public QuestionBank getCompQuestionBankObject(QuestionType questionType, byte[] byteData,
			DifficultyLevel difficultyLevel, SubChapters subChapter, boolean wordSave) {
		QuestionBank questionBank = new QuestionBank();
		questionBank.setDifficultyLevel(difficultyLevel);
		questionBank.setQuestionsFrom(Constants.KALPAKOSH);
		questionBank.setComprehension(new Comprehension());
		questionBank.getComprehension().setComprehensionData(byteData);
		questionBank.setQuestionShortText("Auto Creation Question Comprehension Parent");
		questionBank.setQuestionType(questionType);
		questionBank.setSubChapter(subChapter);
		questionBank.setWordSave(wordSave);
		return questionBank;
	}

	public QuestionBank getQuestionBankObject(QuestionType questionType, byte[] byteData,
			DifficultyLevel difficultyLevel, SubChapters subChapter, boolean wordSave) {
		QuestionBank questionBank = new QuestionBank();
		questionBank.setDifficultyLevel(difficultyLevel);
		questionBank.setQuestionsFrom(Constants.KALPAKOSH);
		questionBank.setQuestionData(new Question());
		questionBank.getQuestionData().setQuestionData(byteData);
		questionBank.setAnswerData(new Answer());
		questionBank.getAnswerData().setAnswerData(byteData);
		questionBank.setQuestionShortText("Auto Creation Question " + System.currentTimeMillis());
		questionBank.setQuestionType(questionType);
		questionBank.setSubChapter(subChapter);
		questionBank.setWordSave(wordSave);
		return questionBank;
	}

	public List<QuestionBank> saveCompQuestions() throws Exception {
		DifficultyLevel difficultyLevel = difficultyLevelRepository.findAll().get(0);
		List<QuestionType> questionTypeList = questionTypeRepository.findAll();
		List<QuestionBank> questionBankList = new ArrayList<QuestionBank>();
		List<SubChapters> subchapters = subChapterRepository.findAll();
		byte[] arqContent = ByteStreams
				.toByteArray(new ClassPathResource("SampleHTMLFiles\\Q1-ARQ_pdf_questionW2.html").getInputStream());
		byte[] mmqContent = ByteStreams.toByteArray(
				new ClassPathResource("SampleHTMLFiles\\Q1-MMQ_pdf_question_pdf_compressW3.html").getInputStream());
		byte[] scqContent = ByteStreams
				.toByteArray(new ClassPathResource("SampleHTMLFiles\\Q2-SCQ_pdf_questionW1.html").getInputStream());
		for (SubChapters subChapter : subchapters) {
			QuestionBank parentQuestion = getCompQuestionBankObject(questionTypeList.get(4), scqContent,
					difficultyLevel, subChapter, false);
			QuestionBank questionSCQ = getQuestionBankObject(questionTypeList.get(1), scqContent, difficultyLevel,
					subChapter, false);
			questionSCQ.getQuestionData().setNoOfOptions(4);
			questionSCQ.getQuestionData().setQuestionOptionText("A&B&C&D");
			questionSCQ.getAnswerData().setAnswerInput("B");
			questionSCQ.getQuestionData().setQuestionData(scqContent);
			questionSCQ.setIsComprehensionChild(true);
			QuestionBank questionARQ = getQuestionBankObject(questionTypeList.get(3), arqContent, difficultyLevel,
					subChapter, false);
			questionARQ.getQuestionData().setQuestionData(arqContent);
			questionARQ.getQuestionData().setNoOfOptions(4);
			questionARQ.getQuestionData().setQuestionOptionText("A&B&C&D");
			questionARQ.getAnswerData().setAnswerInput("B");
			questionARQ.setIsComprehensionChild(true);
			QuestionBank questionMMQ = getQuestionBankObject(questionTypeList.get(5), mmqContent, difficultyLevel,
					subChapter, false);
			questionMMQ.getQuestionData().setQuestionData(arqContent);
			questionMMQ.getQuestionData().setNoOfOptions(4);
			questionMMQ.getQuestionData().setQuestionOptionText("A&B&C&D-P&Q&R&S");
			questionMMQ.getAnswerData().setAnswerInput("AP&BP&AQ");
			questionMMQ.setIsComprehensionChild(true);
			questionMMQ.getQuestionData().setQuestionData(mmqContent);
			List<QuestionBank> childQuestionList = parentQuestion.getChildQuestionList();
			if(childQuestionList==null)
				childQuestionList = new ArrayList<QuestionBank>();
			parentQuestion.setChildQuestionList(childQuestionList);
			childQuestionList.add(questionSCQ);
			childQuestionList.add(questionARQ);
			childQuestionList.add(questionMMQ);

			questionBankList.add(questionBankService.saveQuestion(parentQuestion));
		}
		for (QuestionBank questionMMQ : questionBankList) {
			validateDataUtil.setNullAttributeToQuestionSubChapter(questionMMQ);
		}
		return questionBankList;
	}

	public TestTemplate createSampleTest() throws Exception {
		TestInfo testInfo = null;
		TestTemplate testTemplate = new TestTemplate();
		testTemplate.setClassTypeList(classTypeRepository.findAll());
		testTemplate.setDuration(180l);
		testTemplate.setTestDescription("Auto Created Test");
		testTemplate.setExamType(examTypeRepository.findAll().get(0));
		testTemplate.setTemplateName("Auto Template - " + System.currentTimeMillis());
		testTemplate.setTotalMarks(128);
		testTemplate.setAverageMarks(40);
		testTemplate = testTemplateServiceUtil.saveTestTemplate(testTemplate);
		getSampleTestSectionObject(testTemplate);
		getSampleTestSectionObject(testTemplate);
		//testTemplate = getSampleTestSectionObject(testTemplate).getTestTemplate();
		//testTemplate = getSampleTestSectionObject(testTemplate).getTestTemplate();
		testInfo = testTemplate.getTestInfo();
		List<Batch> batchList = batchRepository.findAll();
		testInfo.setBatchList(batchList.subList(0, 2));
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); 
		testInfo.setStartDate(c.getTime());
		c.add(Calendar.DATE, 5); 
		testInfo.setEndDate(c.getTime());
		testInfo.setTestTemplate(testTemplate);
		testTemplate = testTemplateServiceUtil.saveTestInfo(testInfo);
		testTemplate.setTestSections(null);
		testTemplateServiceUtil.setNullAttributeInTestInfo(testTemplate);
		return testTemplate;
	}

	private TestSection getSampleTestSectionObject(TestTemplate testTemplate) throws Exception {
		TestSection testSection = new TestSection();
		//TestTemplate testTemp = new TestTemplate();
		//testTemp.setTestTemplateId(testTemplate.getTestTemplateId());
		//testSection.setTestTemplate(testTemp);
		//testSection.setMarksPerCorrectPart(4d);
		testSection.setTestTemplate(testTemplate);
		testSection.setMarksForQuestion(4d);
		testSection.setMarksPerSection(64d);
		testSection.setNegativeMarksPerWrongAnswer(-2d);
		testSection.setNoOfQuestions(16);
		testSection.setQuestionSelection(Constants.QuestionSelectionManual);
		testSection.setQuestionType(questionTypeRepository.findByQuestionSearchText(Constants.SCQ));
		testSection.setSectionName("Auto Section-" + System.currentTimeMillis());
		testSection.setTestTemplate(testTemplate);
		List<TestSectionQuestionMapping> testSectionQuestionMapping = new ArrayList<>();
		List<Subject> subjectList = subjectRepository.findAll();
		for (Subject subject : subjectList) {
			testSectionQuestionMapping.add(getTestSectionQuestionMapping(testTemplate.getClassTypeList().get(0),subject));
		}
		testTemplateServiceUtil.saveTestSection(testSection);
		testSection.setTestTemplate(testTemplate);
		testSection.setTestSectionQuestionMapping(testSectionQuestionMapping);
		testTemplateServiceUtil.saveTestSectionQuestion(testSection,false);
		return testSection;
	}

	private TestSectionQuestionMapping getTestSectionQuestionMapping(ClassType classType,Subject subject) throws Exception {
		TestSectionQuestionMapping testSectionQuestionMapping = new TestSectionQuestionMapping();
		testSectionQuestionMapping.setSubject(subject);
		List<QuestionBank> questionBankList = new ArrayList<QuestionBank>();
		for (int i = 0; i < 4; i++) {
			questionBankList.add(addQuestionsForTest(classType,  subject));
		}
		testSectionQuestionMapping.setSelectedQuestions(questionBankList);
		return testSectionQuestionMapping;
	}

	public QuestionBank addQuestionsForTest(ClassType classType, Subject subject) throws Exception {
		DifficultyLevel difficultyLevel = difficultyLevelRepository.findAll().get(0);
		List<QuestionType> questionTypeList = questionTypeRepository.findAll();
		SubChapters subchapters = add1Chapters(classType, subject);
		byte[] scqContent = ByteStreams
				.toByteArray(new ClassPathResource("SampleHTMLFiles\\Q2-SCQ_pdf_question.html").getInputStream());
		QuestionBank questionSCQ = getQuestionBankObject(questionTypeList.get(1), scqContent, difficultyLevel,
				subchapters, true);
		questionSCQ.getQuestionData().setQuestionData(scqContent);
		questionSCQ.getQuestionData().setNoOfOptions(4);
		questionSCQ.getQuestionData().setQuestionOptionText("A&B&C&D");
		questionSCQ.getAnswerData().setAnswerInput("B");
		questionBankService.saveQuestion(questionSCQ);
		return questionSCQ;
	}

	public SubChapters add1Chapters(ClassType classType, Subject subject) {
		//Chapters chapter = chapterRepository.findAll().get(0);
		SubChapters subChapter1 = subChapterRepository.findAll().get(0);
		//chapter.getSubChapterList().add(subChapter1);
		return subChapter1;
	}
	
	public List<StudentProfile> createSampleStudents(String startName, Integer batchCount) throws Exception{
		List<StudentProfile> studentsProfiles = new ArrayList<StudentProfile>();
		List<Batch> batchList = batchRepository.findAll();
		List<ClassType> classTypeList = classTypeRepository.findAll();
		if(batchList!=null){
			int k = 0;
			Integer StudentCount = 0;
			for (Batch batch : batchList) {
				if(k<batchCount){
					StudentProfile studentProfile = getStudentProfileObject (startName,batch,classTypeList.get(0),++StudentCount);
					studentsProfiles.add(studentServiceUtil.saveStudentProfile(studentProfile));
					k++;
				}
			}
		}
		
		return studentsProfiles;
	}

	private StudentProfile getStudentProfileObject(String startName, Batch batch,ClassType classType, int count) {
		StudentProfile studentProfile = new StudentProfile();
		List<Batch> batchList = new ArrayList<Batch>();
		batchList.add(batch);
		studentProfile.setBatchList(batchList);
		studentProfile.setClassType(classType);
		String name = startName+String.format("%03d", count);
		studentProfile.setStudentName(name);
		studentProfile.setPassword(name);
		studentProfile.setRollNo(name);
		ContactDetails contactDetails = new ContactDetails();
		contactDetails.setParentPhoneNo("9986685903");
		contactDetails.setStudentWtsAppNo("9986685903");
		contactDetails.setStudentPhoneNo("9986685903");
		studentProfile.setContactDetails(contactDetails);
		studentProfile.setStudentActiveStatus(Constants.Accepted);
		return studentProfile;
	}
	
	
}