package com.binaryalchemist.programondo.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.binaryalchemist.programondo.models.Material;
import com.binaryalchemist.programondo.models.MaterialGroup;

@Repository
public interface MaterialRepository extends CrudRepository<Material, Long>{
	
}
