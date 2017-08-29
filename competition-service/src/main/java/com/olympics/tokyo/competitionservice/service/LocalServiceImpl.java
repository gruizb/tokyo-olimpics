package com.olympics.tokyo.competitionservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olympics.tokyo.competitionservice.exception.InvalidLocalException;
import com.olympics.tokyo.competitionservice.model.Local;
import com.olympics.tokyo.competitionservice.repository.Locals;

@Service
public class LocalServiceImpl implements LocalService {
	@Autowired
	private Locals locals;

	public Local validateLocal(Local local) throws InvalidLocalException {
		Local newLocal = validateLocalByID(local);
		if (newLocal == null) {
			newLocal = validateLocalByDescription(local);
		}
		return newLocal;
	}

	private Local validateLocalByDescription(Local local) throws InvalidLocalException {
		if (local != null && local.getDescription() != null && !local.getDescription().isEmpty()) {
			List<Local> localByDesc = locals.findByDescription(local.getDescription());
			if (localByDesc != null && !localByDesc.isEmpty()) {
				return localByDesc.get(0);
			} else {
				throw new InvalidLocalException(InvalidLocalException.Reason.INEXISTENT);
			}
		} else {
			throw new InvalidLocalException(InvalidLocalException.Reason.INEXISTENT);

		}
	}

	private Local validateLocalByID(Local local) throws InvalidLocalException {
		if (local != null && local.getId() != null) {
			local = locals.findOne(local.getId());
			if (local != null) {
				return local;
			} else {
				throw new InvalidLocalException(InvalidLocalException.Reason.INEXISTENT);
			}
		}
		return null;
	}

}
