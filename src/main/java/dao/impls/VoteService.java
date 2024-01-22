package dao.impls;

import entity.Vote;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class VoteService extends CommonService<Vote> implements Serializable {
}