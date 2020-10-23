package org.zerock.service;

import org.zerock.domain.Criteria;
import org.zerock.domain.ReplyVO;

import java.util.List;

/**
 * @author sskim
 */
public interface ReplyService {
    public int register(ReplyVO vo);

    public ReplyVO get(Long rno);

    public int remove(Long rno);

    public int modify(ReplyVO vo);

    public List<ReplyVO> getList(Criteria cri, Long bno);
}
