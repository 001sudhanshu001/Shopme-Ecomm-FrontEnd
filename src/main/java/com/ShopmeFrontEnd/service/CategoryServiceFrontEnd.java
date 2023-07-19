package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.dao.CategoryRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryServiceFrontEnd {
    private final CategoryRepoFrontEnd repoFrontEnd;

    public List<Category> listNoChildrenCategory(){
        List<Category> listNoChiildrenCategory = new ArrayList<>();
        List<Category> allEnabled = repoFrontEnd.findAllEnabled();
        allEnabled.forEach(category -> {
            Set<Category> children = category.getChildren();
            if(children == null || children.size() == 0){
                listNoChiildrenCategory.add(category);
            }
        });
        return listNoChiildrenCategory;
    }

    public Category getCategory(String alias){
        return repoFrontEnd.findByAliasEnabled(alias);
    }

    public List<Category> getCategoryParents(Category child) {
        // This will return parent in the top-down order
        // This is required for breadcrumb, First top parent and then lowe ones
        List<Category> listParents = new ArrayList<>();

        Category parent = child.getParent();

        while (parent != null){
            listParents.add(0,parent);
            parent = parent.getParent();
        }

        listParents.add(child);
        return listParents;
    }
}
