export interface RequisitionSummary {
  id: number; requisitionNumber: string; requesterId: number; requesterName: string;
  statusCode: string; statusName: string; title: string; justification: string; neededByDate: string;
}
export interface RequisitionItem {
  id: number; lineNumber: number; itemDescription: string; quantity: number; unitOfMeasure: string; estimatedUnitCost: number;
}
export interface RequisitionDetail extends RequisitionSummary { items: RequisitionItem[]; }
export interface RequisitionStatusOption { code: string; name: string; }
export interface RequisitionOptions { statuses: RequisitionStatusOption[]; }
export interface RequisitionCreateRequest {
  requisitionNumber: string; title: string; justification: string; neededByDate: string; statusCode: string;
}
export type RequisitionUpdateRequest = Omit<RequisitionCreateRequest, 'statusCode'>;
export interface RequisitionItemRequest {
  lineNumber: number; itemDescription: string; quantity: number; unitOfMeasure: string; estimatedUnitCost: number;
}
