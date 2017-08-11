/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flowable.cmmn.engine.impl.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.impl.persistence.entity.data.PlanItemInstanceDataManager;
import org.flowable.cmmn.engine.impl.runtime.PlanItemInstanceQueryImpl;
import org.flowable.cmmn.engine.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.runtime.PlanItemInstanceQuery;
import org.flowable.engine.common.impl.persistence.entity.data.DataManager;

/**
 * @author Joram Barrez
 */
public class PlanItemInstanceEntityManagerImpl extends AbstractCmmnEntityManager<PlanItemInstanceEntity> implements PlanItemInstanceEntityManager {

    protected PlanItemInstanceDataManager planItemInstanceDataManager;

    public PlanItemInstanceEntityManagerImpl(CmmnEngineConfiguration cmmnEngineConfiguration, PlanItemInstanceDataManager planItemInstanceDataManager) {
        super(cmmnEngineConfiguration);
        this.planItemInstanceDataManager = planItemInstanceDataManager;
    }

    @Override
    protected DataManager<PlanItemInstanceEntity> getDataManager() {
        return planItemInstanceDataManager;
    }
    
    @Override
    public List<PlanItemInstanceEntity> findChildPlanItemInstancesForStage(String stagePlanItemInstanceId) {
        return planItemInstanceDataManager.findChildPlanItemInstancesForStage(stagePlanItemInstanceId);
    }
    
    @Override
    public PlanItemInstanceEntity findPlanModelPlanItemInstanceForCaseInstance(String caseInstanceId) {
        return planItemInstanceDataManager.findPlanModelPlanItemInstanceForCaseInstance(caseInstanceId);
    }
    
    @Override
    public void deleteByCaseDefinitionId(String caseDefinitionId) {
        planItemInstanceDataManager.deleteByCaseDefinitionId(caseDefinitionId);
    }
    
    @Override
    public void deleteCascade(PlanItemInstanceEntity planItemInstanceEntity) {
        List<PlanItemInstanceEntity> children = collectChildPlanItemInstances(planItemInstanceEntity);
        for (int i=children.size()-1; i>=0; i--) {
            delete(children.get(i));
        }
        delete(planItemInstanceEntity);
    }
    
    protected List<PlanItemInstanceEntity> collectChildPlanItemInstances(PlanItemInstanceEntity entity) {
        List<PlanItemInstanceEntity> children = new ArrayList<>();
        collectChildPlanItemInstances(entity, children);
        return children;
    }

    protected void collectChildPlanItemInstances(PlanItemInstanceEntity entity, List<PlanItemInstanceEntity> children) {
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            for (PlanItemInstanceEntity child : entity.getChildren()) {
                children.add(child);
                if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                    collectChildPlanItemInstances(child, children);
                }
            }
        }
    }
    
    @Override
    public PlanItemInstanceQuery createPlanItemInstanceQuery() {
        return new PlanItemInstanceQueryImpl(cmmnEngineConfiguration.getCommandExecutor());
    }

    @Override
    public long countByCriteria(PlanItemInstanceQuery planItemInstanceQuery) {
        return planItemInstanceDataManager.countByCriteria((PlanItemInstanceQueryImpl) planItemInstanceQuery);
    }

    @Override
    public List<PlanItemInstance> findByCriteria(PlanItemInstanceQuery planItemInstanceQuery) {
        return planItemInstanceDataManager.findByCriteria((PlanItemInstanceQueryImpl) planItemInstanceQuery);
    }
    
}
