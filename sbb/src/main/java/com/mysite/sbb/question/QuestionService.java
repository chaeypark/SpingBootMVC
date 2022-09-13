package com.mysite.sbb.question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Page<Question> getList(int page, String kw) {
    	List<Sort.Order> sorts = new ArrayList<>();
    	sorts.add(Sort.Order.desc("createDate"));
    	Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
    	Specification<Question> spec = search(kw);
        return this.questionRepository.findAll(spec, pageable);
        /*QuestionRepository에서 쿼리문으로 만든 finaAll 메소드를 실행시키는 경우에 
         * 들어가는 return 값
         * return this.questionRepository.findAllByKeyword(kw, pageable);*/
    }
    
    public Question getQuestion(Integer id) {  
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }
    
    public void create(String subject, String content, SiteUser user) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.questionRepository.save(q);
    }
    
    public void modify(Question question, String subject, String content) {
    	question.setSubject(subject);
    	question.setContent(content);
    	question.setModifyDate(LocalDateTime.now());
    	this.questionRepository.save(question);
    }
    
    public void delete(Question question) {
    	this.questionRepository.delete(question);
    }
    
    public void vote(Question question, SiteUser siteUser) {
    	question.getVoter().add(siteUser);
    	this.questionRepository.save(question);
    }
    
    private Specification<Question> search(String kw){
    	return new Specification<>() {
    		private static final long serialVersionUTD = 1L;
    		@Override
    		public Predicate toPredicate(Root<Question>q, CriteriaQuery<?> query, CriteriaBuilder cb) {
    			query.distinct(true);
    			Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
    			Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
    			Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
    				return cb.or(cb.like(q.get("subject"), "%" + kw + "%"),
    						cb.like(q.get("content"), "%" + kw + "%"),
    						cb.like(u1.get("username"), "%" + kw +"%"),
    						cb.like(u1.get("name"), "%" + kw +"%"),
    						cb.like(a.get("content"), "%" + kw +"%"),
    						cb.like(u2.get("username"), "%" + kw +"%"));
    
    						
    		}
    	};
    }
}